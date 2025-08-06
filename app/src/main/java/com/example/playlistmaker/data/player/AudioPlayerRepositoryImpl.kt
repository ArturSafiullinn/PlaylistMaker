package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.AudioPlayerRepository

class AudioPlayerRepositoryImpl : AudioPlayerRepository {

    private var mediaPlayer: MediaPlayer? = null

    override fun prepare(
        url: String,
        onReady: () -> Unit,
        onCompletion: () -> Unit
    ) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                onReady()
            }
            setOnCompletionListener {
                onCompletion()
            }
            prepareAsync()
        }
    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}