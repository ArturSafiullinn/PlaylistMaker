package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> =
        repository.getPlaylists()

    override suspend fun addToPlaylists(playlist: Playlist): Long {
        return repository.addToPlaylists(playlist)
    }

    override suspend fun deletePlaylistById(playlistId: Long) {
        repository.deletePlaylistById(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) =
        repository.addTrackToPlaylist(playlist, track)

    override fun observePlaylistById(playlistId: Long) =
        repository.observePlaylistById(playlistId)

    override suspend fun updatePlaylistInfo(
        playlistId: Long, name: String, description: String, coverUri: String?
    ) = repository.updatePlaylistInfo(playlistId, name, description, coverUri)
}