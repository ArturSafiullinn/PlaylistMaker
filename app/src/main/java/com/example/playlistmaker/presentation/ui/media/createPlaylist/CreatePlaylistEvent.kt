package com.example.playlistmaker.presentation.ui.media.createPlaylist

sealed interface CreatePlaylistEvent {
    data class ShowDiscardDialog(val title: String, val message: String) : CreatePlaylistEvent
    data class ShowToast(val message: String) : CreatePlaylistEvent
    object CloseScreen : CreatePlaylistEvent
}