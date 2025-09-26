package com.example.playlistmaker.di

import ITunesApi
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val platformModule = module {

    single<SharedPreferences> {
        androidContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    single { Gson() }

    factory { MediaPlayer() }

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    single<ITunesApi> { get<Retrofit>().create(ITunesApi::class.java) }
}