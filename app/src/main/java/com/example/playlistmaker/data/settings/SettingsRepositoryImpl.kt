package com.example.playlistmaker.data.settings

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsRepositoryImpl(
    private val prefs: SharedPreferences
) : SettingsRepository {

    companion object { private const val DARK_THEME_KEY = "dark_theme" }

    private val state = MutableStateFlow(isDarkThemeEnabled())

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == DARK_THEME_KEY) state.value = isDarkThemeEnabled()
    }

    init { prefs.registerOnSharedPreferenceChangeListener(listener) }

    override fun isDarkThemeEnabled(): Boolean =
        prefs.getBoolean(DARK_THEME_KEY, false)

    override fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
    }

    override fun themeEnabledFlow(): Flow<Boolean> = state
}