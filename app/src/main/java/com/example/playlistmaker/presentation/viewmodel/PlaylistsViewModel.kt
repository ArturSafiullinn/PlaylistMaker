package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.*

sealed interface PlaylistsState {
    object Empty : PlaylistsState
    data class Content(val items: List<Playlist>) : PlaylistsState
}

class PlaylistsViewModel(
    private val interactor: com.example.playlistmaker.domain.db.PlaylistsInteractor
) : ViewModel() {

    private val _state = MutableStateFlow<PlaylistsState>(PlaylistsState.Empty)
    val state: StateFlow<PlaylistsState> = _state

    fun observe() {
        interactor.getPlaylists() // Flow<List<Playlist>>
            .onEach { items ->
                _state.value = if (items.isEmpty()) {
                    PlaylistsState.Empty
                } else {
                    PlaylistsState.Content(items)
                }
            }
            .launchIn(viewModelScope)
    }
}