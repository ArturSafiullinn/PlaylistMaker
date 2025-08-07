package com.example.playlistmaker.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.presentation.utils.Creator

class SearchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val searchInteractor = Creator.provideSearchTracksInteractor()
        val historyInteractor = Creator.provideSearchHistoryInteractor(context)

        return SearchViewModel(searchInteractor, historyInteractor) as T
    }
}
