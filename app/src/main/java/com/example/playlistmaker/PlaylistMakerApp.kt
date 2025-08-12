package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.domainModule
import com.example.playlistmaker.di.platformModule
import com.example.playlistmaker.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class PlaylistMakerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PlaylistMakerApp)
            modules(
                listOf(
                    platformModule,
                    dataModule,
                    domainModule,
                    presentationModule
                )
            )
        }
    }
}