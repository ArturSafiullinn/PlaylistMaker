package com.example.playlistmaker.domain.api

interface SettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}