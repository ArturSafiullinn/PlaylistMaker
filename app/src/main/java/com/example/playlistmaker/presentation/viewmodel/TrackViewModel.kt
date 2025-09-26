package com.example.playlistmaker.presentation.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TrackViewModel(
    private val player: AudioPlayerInteractor
) : ViewModel() {

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

    private var timerJob: Job? = null
    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    override fun onCleared() {
        stopTimer()
        player.release()
        super.onCleared()
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

    fun onPause() {
        if (player.isPlaying()) {
            pausePlayer()
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
                delay(300L)
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