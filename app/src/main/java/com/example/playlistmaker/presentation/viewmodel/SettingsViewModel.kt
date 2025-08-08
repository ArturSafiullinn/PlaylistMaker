package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.playlistmaker.domain.api.SettingsInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    val themeState: LiveData<Boolean> =
        settingsInteractor.themeEnabledFlow().asLiveData()

    fun setDarkTheme(enabled: Boolean) =
        settingsInteractor.setDarkThemeEnabled(enabled)
}
