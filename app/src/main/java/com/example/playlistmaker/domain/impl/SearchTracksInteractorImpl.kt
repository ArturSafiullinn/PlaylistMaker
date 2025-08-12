package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchTracksInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class SearchTracksInteractorImpl(
    private val repository: TrackRepository
) : SearchTracksInteractor {

    override fun searchTracks(query: String, callback: (Result<List<Track>>) -> Unit) {
        repository.searchTracks(query, callback)
    }
}