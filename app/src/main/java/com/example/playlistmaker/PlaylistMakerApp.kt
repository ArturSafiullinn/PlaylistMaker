package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
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

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_theme", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}