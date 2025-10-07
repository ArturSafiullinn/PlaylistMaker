package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.FavoritesInteractor
import com.example.playlistmaker.domain.db.FavoritesRepository
import com.example.playlistmaker.domain.models.Track

class FavoritesInteractorImpl(
    private val repo: FavoritesRepository
) : FavoritesInteractor {
    override fun isFavoriteFlow(trackId: Int) = repo.isFavoriteFlow(trackId)
    override fun favoritesFlow() = repo.favoritesFlow()
    override suspend fun add(track: Track) = repo.addToFavorites(track)
    override suspend fun remove(trackId: Int) = repo.removeFromFavorites(trackId)
}