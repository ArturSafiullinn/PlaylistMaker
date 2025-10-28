package com.example.playlistmaker.presentation.ui.media.playlistDetails

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track

data class PlaylistDetailsUiState(
    val playlist: Playlist? = null,
    val tracks: List<Track> = emptyList(),
    val totalDurationMinutes: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)
