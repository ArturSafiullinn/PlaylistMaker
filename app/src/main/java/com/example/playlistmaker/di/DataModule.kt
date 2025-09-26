package com.example.playlistmaker.di

import ITunesApi
import TrackRepositoryImpl
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.playlistmaker.data.history.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.*
import com.example.playlistmaker.data.player.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.domain.api.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single { androidContext().resources } // Resources

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<ITunesApi> { get<Retrofit>().create(ITunesApi::class.java) }

    single<NetworkClient> { NetworkClientImpl(get<ITunesApi>(), androidContext()) }

    factory<AudioPlayerRepository> { AudioPlayerRepositoryImpl(get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }
    single<TrackRepository> { TrackRepositoryImpl(get(), get()) } // NetworkClient + Resources
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_prefs", MODE_PRIVATE)
    }
}