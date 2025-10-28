package com.example.playlistmaker.di
import com.example.playlistmaker.data.favorites.FavoritesRepositoryImpl
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.api.*
import com.example.playlistmaker.domain.db.FavoritesInteractor
import com.example.playlistmaker.domain.db.FavoritesRepository
import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.impl.*
import org.koin.dsl.module

val domainModule = module {

    factory<AudioPlayerInteractor> { AudioPlayerInteractorImpl(get()) }

    single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }

    single<SearchTracksInteractor> { SearchTracksInteractorImpl(get()) }

    single<SettingsInteractor> { SettingsInteractorImpl(get()) }

    single<FavoritesRepository> { FavoritesRepositoryImpl(get(), get()) }

    single<FavoritesInteractor> { FavoritesInteractorImpl (get()) }

    single< PlaylistsInteractor> { PlaylistsInteractorImpl (get()) }

}