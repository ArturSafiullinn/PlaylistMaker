package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.FavoritesInteractor
import com.example.playlistmaker.domain.db.FavoritesRepository
import com.example.playlistmaker.domain.models.Track

class FavoritesInteractorImpl(
    private val repo: FavoritesRepository
) : FavoritesInteractor {
    override fun isFavorite(trackId: Long) = repo.isFavorite(trackId)
    override fun getFavorites() = repo.getFavorites()
    override suspend fun add(track: Track) = repo.addToFavorites(track)
    override suspend fun remove(trackId: Long) = repo.removeFromFavorites(trackId)
}