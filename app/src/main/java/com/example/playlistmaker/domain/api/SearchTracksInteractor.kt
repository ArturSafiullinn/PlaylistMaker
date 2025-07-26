package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchTracksInteractor {
    fun searchTracks(query: String, callback: (Result<List<Track>>) -> Unit)
}