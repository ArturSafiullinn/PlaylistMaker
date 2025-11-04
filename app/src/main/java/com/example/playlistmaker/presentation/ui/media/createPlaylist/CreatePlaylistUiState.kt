package com.example.playlistmaker.presentation.ui.media.createPlaylist

import android.net.Uri

data class CreatePlaylistUiState(
    val playlistId: Long? = null,
    val isEditMode: Boolean = false,
    val name: String = "",
    val description: String = "",
    val initialCoverPath: String? = null,
    val pickedCover: Uri? = null,
    val isLoading: Boolean = false,
    val isCreateEnabled: Boolean = false,
    val hasUnsavedChanges: Boolean = false
) {
    val coverToShow: Any?
        get() = pickedCover ?: initialCoverPath
}
