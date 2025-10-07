package com.example.playlistmaker.data.favorites

import com.example.playlistmaker.data.TrackDbConverter
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.domain.db.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val dao: TrackDao,
    private val converter: TrackDbConverter
) : FavoritesRepository {

    override fun isFavoriteFlow(trackId: Int) = dao.observeIsFavorite(trackId)

    override fun favoritesFlow(): Flow<List<Track>> =
        dao.observeFavorites().map { list -> list.map(converter::map) }

    override suspend fun addToFavorites(track: Track) {
        dao.insert(converter.map(track))
    }

    override suspend fun removeFromFavorites(trackId: Int) {
        dao.deleteById(trackId)
    }
}