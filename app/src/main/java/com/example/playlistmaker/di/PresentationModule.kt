package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.viewmodel.MainViewModel
import com.example.playlistmaker.presentation.viewmodel.SearchViewModel
import com.example.playlistmaker.presentation.viewmodel.SettingsViewModel
import com.example.playlistmaker.presentation.viewmodel.TrackViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    viewModel { MainViewModel(get()) }

    viewModel { SearchViewModel(get(), get()) }

    viewModel { SettingsViewModel(get()) }

    viewModel { TrackViewModel(get()) }
}