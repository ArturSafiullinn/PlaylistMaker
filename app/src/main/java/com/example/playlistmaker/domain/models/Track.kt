package com.example.playlistmaker.domain.models

import java.io.Serializable


data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl: String,
    val collectionName: String = "",
    val releaseDate: String = "",
    val genre: String = "",
    val country: String = "",
    val previewUrl: String
) : Serializable
