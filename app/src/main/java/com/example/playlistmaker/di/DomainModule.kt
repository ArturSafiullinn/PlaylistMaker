package com.example.playlistmaker.di
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.api.*
import com.example.playlistmaker.domain.impl.*
import org.koin.dsl.module

val domainModule = module {

    single<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<SearchTracksInteractor> {
        SearchTracksInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}
