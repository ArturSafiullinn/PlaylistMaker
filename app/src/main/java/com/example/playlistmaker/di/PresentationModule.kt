package com.example.playlistmaker.di

import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.viewmodel.FavoritesViewModel
import com.example.playlistmaker.presentation.viewmodel.MainViewModel
import com.example.playlistmaker.presentation.viewmodel.MediaViewModel
import com.example.playlistmaker.presentation.viewmodel.PlaylistsViewModel
import com.example.playlistmaker.presentation.viewmodel.SearchViewModel
import com.example.playlistmaker.presentation.viewmodel.SettingsViewModel
import com.example.playlistmaker.presentation.viewmodel.TrackViewModel
import com.example.playlistmaker.presentation.ui.media.createPlaylist.CreatePlaylistStrings
import com.example.playlistmaker.presentation.ui.media.createPlaylist.CoverStorage
import com.example.playlistmaker.data.storage.CoverStorageImpl
import com.example.playlistmaker.presentation.viewmodel.CreatePlaylistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    viewModel { MainViewModel(get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { TrackViewModel(get(), get(), get(), get()) }
    viewModel { MediaViewModel() }
    viewModel { FavoritesViewModel(get()) }
    viewModel { PlaylistsViewModel(get()) }

    single<CoverStorage> { CoverStorageImpl(androidContext()) }
    factory<CreatePlaylistViewModel.Strings> { CreatePlaylistStrings(androidContext()) }

    viewModel {
        CreatePlaylistViewModel(
            playlistsInteractor = get(),
            coverStorage = get(),
            strings = get()
        )
    }

    factory<TrackViewModel.Strings> {
        val ctx = androidContext()
        object : TrackViewModel.Strings {
            override fun addedToPlaylist(name: String): String =
                ctx.getString(R.string.added_to_playlist_fmt, name)
            override fun alreadyInPlaylist(name: String): String =
                ctx.getString(R.string.already_in_playlist_fmt, name)
        }
    }
}