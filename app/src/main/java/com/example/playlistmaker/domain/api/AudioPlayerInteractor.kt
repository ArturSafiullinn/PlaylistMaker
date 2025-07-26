package com.example.playlistmaker.domain.api

interface AudioPlayerInteractor {
    fun prepare(url: String, onReady: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun release()
}