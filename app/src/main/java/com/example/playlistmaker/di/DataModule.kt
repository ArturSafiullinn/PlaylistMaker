package com.example.playlistmaker.di
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.playlistmaker.data.history.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.*
import com.example.playlistmaker.data.player.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.domain.api.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<AudioPlayerRepository> { AudioPlayerRepositoryImpl(get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }
    single<NetworkClient> { NetworkClientImpl(get()) }
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_prefs", MODE_PRIVATE)
    }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}
