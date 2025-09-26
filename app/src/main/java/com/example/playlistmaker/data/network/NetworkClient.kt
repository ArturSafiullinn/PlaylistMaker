package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.network.core.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}
