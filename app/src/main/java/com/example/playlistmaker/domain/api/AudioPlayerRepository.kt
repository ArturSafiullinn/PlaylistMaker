package com.example.playlistmaker.domain.api

interface AudioPlayerRepository {
    fun prepare(url: String, onReady: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}