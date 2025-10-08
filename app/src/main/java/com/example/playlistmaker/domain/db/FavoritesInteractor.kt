package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun isFavorite(trackId: Int): Flow<Boolean>
    fun getFavorites(): Flow<List<Track>>
    suspend fun add(track: Track)
    suspend fun remove(trackId: Int)
}