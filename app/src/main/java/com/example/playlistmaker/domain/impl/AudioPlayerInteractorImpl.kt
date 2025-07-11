package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.AudioPlayerInteractor

class AudioPlayerInteractorImpl : AudioPlayerInteractor {
    private val mediaPlayer = MediaPlayer()
    private var isPrepared = false

    override fun prepare(url: String, onReady: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            isPrepared = true
            onReady()
        }
        mediaPlayer.setOnCompletionListener {
            isPrepared = true
            onCompletion()
        }
    }

    override fun play() {
        if (isPrepared && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun release() {
        mediaPlayer.release()
    }
}
