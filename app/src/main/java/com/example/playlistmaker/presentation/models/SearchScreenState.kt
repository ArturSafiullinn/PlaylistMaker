package com.example.playlistmaker.presentation.models

import com.example.playlistmaker.domain.models.Track

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    object Empty : SearchScreenState()
    data class Content(val tracks: List<Track>) : SearchScreenState()
    object Error : SearchScreenState()
}
