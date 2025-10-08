package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavoritesInteractor
import com.example.playlistmaker.presentation.models.FavoritesScreenState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor,
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesScreenState>()
    val state: LiveData<FavoritesScreenState> = _state

    init {
        observeFavorites()
    }

    private fun observeFavorites() = viewModelScope.launch {
        favoritesInteractor.getFavorites()
            .distinctUntilChanged()
            .collect { list ->
                _state.value = if (list.isEmpty()) {
                    FavoritesScreenState.Empty
                } else {
                    FavoritesScreenState.Content(list)
                }
            }
    }
}