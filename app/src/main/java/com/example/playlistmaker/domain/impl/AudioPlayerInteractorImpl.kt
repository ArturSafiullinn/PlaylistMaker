package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.AudioPlayerRepository

class AudioPlayerInteractorImpl(
    private val repository: AudioPlayerRepository
) : AudioPlayerInteractor {

    override fun prepare(url: String, onReady: () -> Unit, onCompletion: () -> Unit) {
        repository.prepare(url, onReady, onCompletion)
    }
    override fun play() = repository.play()
    override fun pause() = repository.pause()
    override fun stop() = repository.stop()
    override fun isPlaying(): Boolean = repository.isPlaying()
    override fun getCurrentPosition(): Int = repository.getCurrentPosition()
    override fun release() = repository.release()
}