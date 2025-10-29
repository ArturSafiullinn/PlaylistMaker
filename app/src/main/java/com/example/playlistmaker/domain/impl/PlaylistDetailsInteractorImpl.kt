package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlaylistDetailsInteractor
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistDetailsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistDetailsInteractor {

    override fun observePlaylistWithTracks(playlistId: Long): Flow<Pair<Playlist, List<Track>>> {
        return repository.observePlaylistWithTracks(playlistId)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        repository.removeTrackFromPlaylist(playlistId, trackId)
    }
}
