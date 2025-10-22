package com.example.playlistmaker.presentation.ui.media.createPlaylist

import android.net.Uri

data class CreatePlaylistUiState(
    val name: String = "",
    val description: String = "",
    val pickedCover: Uri? = null,
    val isCreateEnabled: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val isLoading: Boolean = false
)