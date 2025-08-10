package com.example.playlistmaker.presentation.models

@kotlinx.parcelize.Parcelize
data class UiTrack(
    val id: Int,
    val trackName: String,
    val artistName: String,
    val artworkUrl: String,
    val previewUrl: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val genre: String?,
    val country: String?
) : android.os.Parcelable