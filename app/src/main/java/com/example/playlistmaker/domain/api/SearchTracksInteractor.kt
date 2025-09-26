package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchTracksInteractor {
    fun searchTracks(query: String): Flow<Resource<List<Track>>>
}