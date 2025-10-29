package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addToPlaylists(playlist: Playlist): Long
    suspend fun deletePlaylistById(playlistId: Long)
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean
    fun observePlaylistWithTracks(playlistId: Long): Flow<Pair<Playlist, List<Track>>>
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
}