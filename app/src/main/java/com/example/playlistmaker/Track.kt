package com.example.playlistmaker
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl: String,
    val collectionName: String = "",
    val releaseDate: String = "",
    val genre: String = "",
    val country: String = "",
    val previewUrl: String) : Parcelable
