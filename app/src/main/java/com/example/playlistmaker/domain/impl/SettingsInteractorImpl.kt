package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean = repository.isDarkThemeEnabled()

    override fun setDarkThemeEnabled(enabled: Boolean) {
        repository.setDarkThemeEnabled(enabled)
    }

    override fun themeEnabledFlow(): Flow<Boolean> = repository.themeEnabledFlow()
}