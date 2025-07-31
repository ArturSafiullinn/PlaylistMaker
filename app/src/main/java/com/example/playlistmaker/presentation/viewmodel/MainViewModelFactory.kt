package com.example.playlistmaker.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.presentation.utils.Creator

class MainViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val sharedPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return MainViewModel(Creator.provideSettingsInteractor(sharedPrefs)) as T
    }
}
