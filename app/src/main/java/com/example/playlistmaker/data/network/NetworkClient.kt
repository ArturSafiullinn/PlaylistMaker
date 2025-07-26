package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.SearchResponse

interface NetworkClient {
    fun searchTracks(query: String, callback: (Result<SearchResponse>) -> Unit)
}
