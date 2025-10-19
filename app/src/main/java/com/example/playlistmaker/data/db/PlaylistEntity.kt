package com.example.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val playlistId: Long,
    val name: String,
    val description: String,
    val playlistCover: String,
    val playlistTracks: String?,
    val playlistLength: Int
)