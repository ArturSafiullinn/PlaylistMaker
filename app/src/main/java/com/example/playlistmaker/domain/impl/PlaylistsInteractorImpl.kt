package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> =
        repository.getPlaylists()

    override suspend fun addToPlaylists(playlist: Playlist) {
        repository.addToPlaylists(playlist)
    }

    override suspend fun deletePlaylistById(playlistId: Long) {
        repository.deletePlaylistById(playlistId)
    }
}