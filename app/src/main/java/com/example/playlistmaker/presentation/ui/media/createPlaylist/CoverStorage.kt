package com.example.playlistmaker.presentation.ui.media.createPlaylist

import android.net.Uri

interface CoverStorage {
    suspend fun saveCover(src: Uri): String?
}