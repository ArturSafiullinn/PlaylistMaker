package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track

interface PlaylistDetailsInteractor {
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    fun observePlaylistWithTracks(playlistId: Long): kotlinx.coroutines.flow.Flow<Pair<Playlist, List<Track>>>
}
