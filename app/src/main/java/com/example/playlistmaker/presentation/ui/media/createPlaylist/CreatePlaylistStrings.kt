package com.example.playlistmaker.presentation.ui.media.createPlaylist

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.viewmodel.CreatePlaylistViewModel

class CreatePlaylistStrings(private val context: Context) : CreatePlaylistViewModel.Strings {
    override val finishCreationTitle: String =
        context.getString(R.string.finish_playlist_creation_title)

    override val unsavedDataMsg: String =
        context.getString(R.string.unsaved_data_will_be_lost)

    override fun playlistCreatedFormat(name: String): String =
        context.getString(R.string.playlist_created_format, name)
}
