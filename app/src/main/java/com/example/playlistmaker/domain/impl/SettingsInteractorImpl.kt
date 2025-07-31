package com.example.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsInteractor

class SettingsInteractorImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsInteractor {

    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
    }

    private val listeners = mutableListOf<(Boolean) -> Unit>()

    private val preferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == DARK_THEME_KEY) {
                val isDark = isDarkThemeEnabled()
                listeners.forEach { it(isDark) }
            }
        }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
    }

    override fun observeThemeChanges(callback: (Boolean) -> Unit) {
        listeners.add(callback)
        callback(isDarkThemeEnabled())
    }
}