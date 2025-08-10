package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.AudioPlayerRepository

class AudioPlayerRepositoryImpl(
    private var mediaPlayer: MediaPlayer
) : AudioPlayerRepository {

    override fun prepare(url: String, onReady: () -> Unit, onCompletion: () -> Unit) {
        release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { onReady() }
            setOnCompletionListener { onCompletion() }
            setOnErrorListener { _, _, _ -> release(); true }
            prepareAsync()
        }
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun stop() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}
