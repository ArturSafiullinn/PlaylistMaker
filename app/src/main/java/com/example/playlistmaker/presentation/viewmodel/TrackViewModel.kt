package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrackViewModel(
    private val player: AudioPlayerInteractor
) : ViewModel() {

    sealed class UiState {
        object Ready : UiState()
        object Playing : UiState()
        object Paused : UiState()
        object Finished : UiState()
        data class TimeUpdate(val millis: Int) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Paused)
    val uiState: StateFlow<UiState> = _uiState

    fun preparePlayer(url: String) {
        player.prepare(
            url = url,
            onReady = {
                _uiState.value = UiState.Ready
            },
            onCompletion = {
                _uiState.value = UiState.Finished
            }
        )
    }

    fun togglePlayback() {
        if (player.isPlaying()) {
            player.pause()
            _uiState.value = UiState.Paused
        } else {
            player.play()
            _uiState.value = UiState.Playing
        }
    }

    fun updatePlaybackTime() {
        viewModelScope.launch {
            _uiState.emit(UiState.TimeUpdate(player.getCurrentPosition()))
        }
    }

    fun releasePlayer() {
        player.release()
    }
}
