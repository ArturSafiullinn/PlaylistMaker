package com.example.playlistmaker.presentation.models

import com.example.playlistmaker.domain.models.Track

sealed interface FavoritesScreenState {
    object Empty : FavoritesScreenState
    data class Content(val tracks: List<Track>) : FavoritesScreenState
}