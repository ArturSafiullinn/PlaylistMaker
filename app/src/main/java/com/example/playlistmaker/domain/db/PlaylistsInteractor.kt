package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    fun observePlaylistById(playlistId: Long): Flow<Playlist>
    suspend fun addToPlaylists(playlist: Playlist): Long
    suspend fun updatePlaylistInfo(playlistId: Long, name: String, description: String, coverUri: String?)
    suspend fun deletePlaylistById(playlistId: Long)
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean
}