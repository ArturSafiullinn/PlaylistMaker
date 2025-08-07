package com.example.playlistmaker.di

import com.example.playlistmaker.data.history.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.NetworkClientImpl
import com.example.playlistmaker.data.network.TrackRepositoryImpl
import com.example.playlistmaker.data.player.AudioPlayerRepositoryImpl
import com.example.playlistmaker.domain.api.AudioPlayerRepository
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TrackRepository
import org.koin.dsl.module
import retrofit2.Retrofit

val dataModule = module {

    single<AudioPlayerRepository> { AudioPlayerRepositoryImpl(get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }

    single<NetworkClient> { NetworkClientImpl(get()) }
    single<TrackRepository> { TrackRepositoryImpl(get()) }
}
