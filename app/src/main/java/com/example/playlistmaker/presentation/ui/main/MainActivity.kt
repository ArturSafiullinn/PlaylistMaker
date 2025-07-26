package com.example.playlistmaker.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.presentation.ui.media.MediaActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.search.SearchActivity
import com.example.playlistmaker.presentation.ui.settings.SettingsActivity
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.presentation.utils.Creator

class MainActivity : AppCompatActivity() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPrefs = getSharedPreferences("settings", MODE_PRIVATE)
        settingsInteractor = Creator.provideSettingsInteractor(sharedPrefs)

        applyTheme(settingsInteractor.isDarkThemeEnabled())

        findViewById<Button>(R.id.search).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<Button>(R.id.media).setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        findViewById<Button>(R.id.settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
