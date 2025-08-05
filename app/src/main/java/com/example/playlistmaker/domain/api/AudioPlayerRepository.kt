package com.example.playlistmaker.domain.api

interface AudioPlayerRepository {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun stop()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}