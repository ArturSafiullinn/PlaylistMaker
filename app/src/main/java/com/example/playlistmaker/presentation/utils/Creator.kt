package com.example.playlistmaker.presentation.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.history.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.NetworkClientImpl
import com.example.playlistmaker.data.network.TrackRepositoryImpl
import com.example.playlistmaker.data.player.AudioPlayerRepositoryImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchTracksInteractor
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractorImpl

object Creator {
    private fun getTracksRepository(): TrackRepository {
        return TrackRepositoryImpl(NetworkClientImpl())
    }

    fun provideSearchTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(getTracksRepository())
    }

    fun provideSettingsInteractor(sharedPreferences: SharedPreferences): SettingsInteractor {
        return SettingsInteractorImpl(sharedPreferences)
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        val repository = AudioPlayerRepositoryImpl()
        return AudioPlayerInteractorImpl(repository)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val repository = SearchHistoryRepositoryImpl(context)
        return SearchHistoryInteractorImpl(repository)
    }
}