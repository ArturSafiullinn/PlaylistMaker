package com.example.playlistmaker.presentation.models

import com.example.playlistmaker.domain.models.Track

sealed interface SearchScreenState {
    object Loading : SearchScreenState
    object Empty : SearchScreenState
    object Error : SearchScreenState
    data class Content(val tracks: List<Track>) : SearchScreenState
    data class History(val tracks: List<Track>) : SearchScreenState
}

