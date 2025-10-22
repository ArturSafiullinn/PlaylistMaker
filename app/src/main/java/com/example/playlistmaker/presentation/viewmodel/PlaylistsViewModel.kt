package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface PlaylistsState {
    object Empty : PlaylistsState
    data class Content(val items: List<Playlist>) : PlaylistsState
}

class PlaylistsViewModel(
    private val interactor: PlaylistsInteractor
) : ViewModel() {

    private val _state = MutableStateFlow<PlaylistsState>(PlaylistsState.Empty)
    val state: StateFlow<PlaylistsState> = _state.asStateFlow()

    fun observe() {
        viewModelScope.launch {
            interactor.getPlaylists() // Flow<List<Playlist>>
                .map { list ->
                    val sorted = list.sortedByDescending { it.playlistId ?: Long.MIN_VALUE }
                    if (sorted.isEmpty()) PlaylistsState.Empty else PlaylistsState.Content(sorted)
                }
                .collect { _state.value = it }
        }
    }
}
