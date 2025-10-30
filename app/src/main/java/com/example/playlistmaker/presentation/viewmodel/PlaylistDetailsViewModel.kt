package com.example.playlistmaker.presentation.ui.media.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistDetailsInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media.playlistDetails.PlaylistDetailsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val interactor: PlaylistDetailsInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistDetailsUiState())
    val state: StateFlow<PlaylistDetailsUiState> = _state

    private var job: Job? = null

    fun load(playlistId: Long) {
        job?.cancel()
        job = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            interactor.observePlaylistWithTracks(playlistId).collectLatest { (playlist, tracks) ->
                val totalMinutes = tracks.sumOf { (it.trackTime ?: 0) / 60000 }
                _state.update {
                    it.copy(
                        playlist = playlist,
                        tracks = tracks,
                        totalDurationMinutes = totalMinutes.toInt(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun removeTrack(track: Track) {
        val playlist = _state.value.playlist ?: return
        viewModelScope.launch {
            runCatching {
                interactor.removeTrackFromPlaylist(playlist.playlistId, track.trackId)
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun buildShareText(): String {
        val playlist = _state.value.playlist ?: return ""
        val tracks = _state.value.tracks
        if (tracks.isEmpty()) return ""

        val header = "Плейлист: ${playlist.name}\n"
        val desc =
            if (playlist.description.isNotBlank()) "Описание: ${playlist.description}\n" else ""
        val list = tracks.joinToString("\n") { "• ${it.artistName} — ${it.trackName}" }

        return buildString {
            append(header)
            append(desc)
            append("Треков: ${tracks.size}\n\n")
            append(list)
        }
    }

    fun deletePlaylist() {
        val p = _state.value.playlist ?: return
        viewModelScope.launch {
            runCatching { interactor.deletePlaylist(p.playlistId) }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }
}