package com.example.playlistmaker.presentation.models

@kotlinx.parcelize.Parcelize
data class UiTrack(
    val id: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
    val artworkUrl: String,
    val collectionName: String?,
    val releaseDate: String?,
    val genre: String?,
    val country: String?,
    val previewUrl: String?
) : android.os.Parcelable