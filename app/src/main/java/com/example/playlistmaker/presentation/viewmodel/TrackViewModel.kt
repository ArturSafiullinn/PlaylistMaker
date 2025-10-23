package com.example.playlistmaker.presentation.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.db.FavoritesInteractor
import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TrackViewModel(
    private val player: AudioPlayerInteractor,
    private val favorites: FavoritesInteractor,
    private val playlists: PlaylistsInteractor,           // NEW
    private val strings: Strings
) : ViewModel() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

    sealed class PlayerState(
        val isPlayButtonEnabled: Boolean,
        @DrawableRes val buttonIcon: Int,
        val progress: String
    ) {
        class Default : PlayerState(
            isPlayButtonEnabled = false,
            buttonIcon = R.drawable.play_button,
            progress = "00:00"
        )

        class Prepared : PlayerState(
            isPlayButtonEnabled = true,
            buttonIcon = R.drawable.play_button,
            progress = "00:00"
        )

        class Playing(progress: String) : PlayerState(
            isPlayButtonEnabled = true,
            buttonIcon = R.drawable.pause_button,
            progress = progress
        )

        class Paused(progress: String) : PlayerState(
            isPlayButtonEnabled = true,
            buttonIcon = R.drawable.play_button,
            progress = progress
        )
    }
    sealed interface UiEvent {
        data class ShowToast(val msg: String) : UiEvent
        object OpenBottomSheet : UiEvent
        object CloseBottomSheet : UiEvent
        object OpenCreatePlaylist : UiEvent
    }

    interface Strings {
        fun addedToPlaylist(name: String): String
        fun alreadyInPlaylist(name: String): String
    }

    private val _events = kotlinx.coroutines.channels.Channel<UiEvent>(kotlinx.coroutines.channels.Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _playlists = MutableStateFlow<List<com.example.playlistmaker.domain.models.Playlist>>(emptyList())
    val playlistsFlow: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private var playlistsJob: Job? = null

    private var timerJob: Job? = null
    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    private var currentTrack: Track? = null


    override fun onCleared() {
        stopTimer()
        player.release()
        super.onCleared()
    }

    fun bindTrack(track: Track) {
        currentTrack = track
        viewModelScope.launch {
            favorites.isFavorite(track.trackId).collect {
                _isFavorite.postValue(it)
            }
        }
    }

    fun preparePlayer(url: String) {
        player.prepare(
            url = url,
            onReady = {
                playerState.postValue(PlayerState.Prepared())
            },
            onCompletion = {
                stopTimer()
                playerState.postValue(PlayerState.Prepared())
            }
        )
    }

    fun onPlayButtonClicked() {
        when (playerState.value) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> Unit
        }
    }

    fun onLikeButtonClicked() {
        val track = currentTrack ?: return
        viewModelScope.launch {
            val nowFav = isFavorite.value == true
            if (nowFav) {
                favorites.remove(track.trackId)
            } else {
                favorites.add(track)
            }
        }
    }

    fun onPause() {
        if (player.isPlaying()) {
            pausePlayer()
        }
    }

    fun onAddToPlaylistClicked() {
        playlistsJob?.cancel()
        playlistsJob = viewModelScope.launch {
            playlists.getPlaylists().collect { _playlists.value = it }
        }
        viewModelScope.launch { _events.send(UiEvent.OpenBottomSheet) }
    }

    fun onNewPlaylistClicked() {
        viewModelScope.launch { _events.send(UiEvent.OpenCreatePlaylist) }
    }

    fun onPickPlaylist(playlist: com.example.playlistmaker.domain.models.Playlist) {
        val track = currentTrack ?: return
        // Быстрая проверка «уже есть?» по csv-id внутри плейлиста
        val ids = com.example.playlistmaker.data.util.IdsCsv.fromCsv(playlist.playlistTracks)
        if (ids.contains(track.trackId)) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowToast(strings.alreadyInPlaylist(playlist.name)))
                _events.send(UiEvent.CloseBottomSheet)
            }
            return
        }
        viewModelScope.launch {
            val added = playlists.addTrackToPlaylist(playlist, track)
            if (added) _events.send(UiEvent.ShowToast(strings.addedToPlaylist(playlist.name)))
            else _events.send(UiEvent.ShowToast(strings.alreadyInPlaylist(playlist.name)))
            _events.send(UiEvent.CloseBottomSheet)
        }
    }


    private fun startPlayer() {
        player.play()
        playerState.postValue(PlayerState.Playing(formatMillis(player.getCurrentPosition())))
        startTimer()
    }

    private fun pausePlayer() {
        player.pause()
        stopTimer()
        playerState.postValue(PlayerState.Paused(formatMillis(player.getCurrentPosition())))
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (isActive && player.isPlaying()) {
                playerState.postValue(
                    PlayerState.Playing(formatMillis(player.getCurrentPosition()))
                )
                delay(CLICK_DEBOUNCE_DELAY)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun formatMillis(millis: Int): String {
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis.toLong())
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}