package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addToPlaylists(playlist: Playlist)
    suspend fun deletePlaylistById(playlistId: Long)
}