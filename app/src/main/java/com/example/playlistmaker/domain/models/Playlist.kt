package com.example.playlistmaker.domain.models

data class Playlist (
    val playlistId: Long?,
    val name: String,
    val description: String,
    val coverUri: String? = null,
    val playlistTracks: String? = null,
    val playlistLength: Int = 0
)