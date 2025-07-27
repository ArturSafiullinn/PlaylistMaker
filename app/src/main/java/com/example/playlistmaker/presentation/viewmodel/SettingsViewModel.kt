package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.SettingsInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> = _themeState

    init {
        _themeState.value = settingsInteractor.isDarkThemeEnabled()
    }

    fun setDarkTheme(enabled: Boolean) {
        settingsInteractor.setDarkThemeEnabled(enabled)
        _themeState.value = enabled
    }
}