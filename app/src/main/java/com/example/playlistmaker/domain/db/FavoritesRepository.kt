package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun isFavoriteFlow(trackId: Int): Flow<Boolean>
    fun favoritesFlow(): Flow<List<Track>>
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(trackId: Int)
}