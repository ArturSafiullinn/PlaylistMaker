package com.example.playlistmaker.di

import ITunesApi
import TrackRepositoryImpl
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.playlistmaker.data.PlaylistDbConverter
import com.example.playlistmaker.data.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.dao.PlaylistsDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.favorites.FavoritesRepositoryImpl
import com.example.playlistmaker.data.history.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.NetworkClientImpl
import com.example.playlistmaker.data.player.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.playlists.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.domain.api.AudioPlayerRepository
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.db.FavoritesRepository
import com.example.playlistmaker.domain.db.PlaylistsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    // Resources
    single { androidContext().resources }

    // Retrofit + API
    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<ITunesApi> { get<Retrofit>().create(ITunesApi::class.java) }
    single<NetworkClient> { NetworkClientImpl(get(), androidContext()) }

    // Room
    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single<TrackDao> { get<AppDatabase>().trackDao() }
    single<PlaylistsDao> { get<AppDatabase>().playlistsDao() }


    // Converters
    single { TrackDbConverter() }
    single { PlaylistDbConverter() }

    // Repositories
    factory<AudioPlayerRepository> { AudioPlayerRepositoryImpl(get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }
    single<TrackRepository> { TrackRepositoryImpl(get(), get()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get(), get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<PlaylistsRepository> { PlaylistsRepositoryImpl(get(), get()) }

    // SharedPreferences
    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_prefs", MODE_PRIVATE)
    }
}