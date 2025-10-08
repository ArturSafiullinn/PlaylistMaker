package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun isFavorite(trackId: Int): Flow<Boolean>
    fun getFavorites(): Flow<List<Track>>
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(trackId: Int)
}