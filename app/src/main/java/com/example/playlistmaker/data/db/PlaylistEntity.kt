package com.example.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val playlistId:Long = 0L,
    val name: String,
    val description: String,
    val coverUri: String?,
    val playlistTracks: String? = null,
    val playlistLength: Int = 0
)