package com.example.playlistmaker.domain.models

data class Playlist (
    val playlistId: Long,
    val name: String,
    val description: String,
    val playlistCover: String,
    val playlistTracks: String?,
    val playlistLength: Int
)