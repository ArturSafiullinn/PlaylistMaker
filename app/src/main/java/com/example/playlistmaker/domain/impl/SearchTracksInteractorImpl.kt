package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchTracksInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

class SearchTracksInteractorImpl(
    private val repository: TrackRepository
) : SearchTracksInteractor {

    override fun searchTracks(query: String): Flow<Resource<List<Track>>> {
        return repository.searchTracks(query)
    }
}