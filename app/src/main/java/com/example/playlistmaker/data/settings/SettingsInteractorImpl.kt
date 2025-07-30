package com.example.playlistmaker.data.settings

import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.domain.api.SettingsInteractor

class SettingsInteractorImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsInteractor {

    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        Log.d("SetDarkTheme", "Интерактор записал значение в SharedPreferences: $enabled")
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
    }
}