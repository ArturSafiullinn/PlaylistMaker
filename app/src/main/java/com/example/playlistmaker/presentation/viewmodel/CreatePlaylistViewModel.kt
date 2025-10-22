package com.example.playlistmaker.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.ui.media.createPlaylist.CoverStorage
import com.example.playlistmaker.presentation.ui.media.createPlaylist.CreatePlaylistEvent
import com.example.playlistmaker.presentation.ui.media.createPlaylist.CreatePlaylistUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val coverStorage: CoverStorage,
    private val strings: Strings
) : ViewModel() {

    private val _state = MutableStateFlow(CreatePlaylistUiState())
    val state: StateFlow<CreatePlaylistUiState> = _state.asStateFlow()

    private val _events = Channel<CreatePlaylistEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onNameChanged(value: String) = updateState(name = value)
    fun onDescriptionChanged(value: String) = updateState(description = value)
    fun onCoverPicked(uri: Uri?) = updateState(pickedCover = uri)

    fun onBackPressed() {
        if (_state.value.hasUnsavedChanges && !_state.value.isLoading) {
            viewModelScope.launch {
                _events.send(
                    CreatePlaylistEvent.ShowDiscardDialog(
                        title = strings.finishCreationTitle,
                        message = strings.unsavedDataMsg
                    )
                )
            }
        } else {
            confirmDiscardAndClose()
        }
    }

    fun confirmDiscardAndClose() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    name = "",
                    description = "",
                    pickedCover = null,
                    hasUnsavedChanges = false,
                    isCreateEnabled = false,
                    isLoading = false
                )
            }
            _events.send(CreatePlaylistEvent.CloseScreen)
        }
    }

    fun createPlaylist() {
        val current = _state.value
        if (!current.isCreateEnabled || current.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val savedCoverPath = current.pickedCover?.let { coverStorage.saveCover(it) }

                val playlist = Playlist(
                    playlistId = null,
                    name = current.name.trim(),
                    description = current.description.trim(),
                    coverUri = savedCoverPath,
                    playlistLength = 0,
                    playlistTracks = null
                )

                playlistsInteractor.addToPlaylists(playlist)

                _state.update {
                    it.copy(
                        name = "",
                        description = "",
                        pickedCover = null,
                        hasUnsavedChanges = false,
                        isCreateEnabled = false
                    )
                }

                _events.send(
                    CreatePlaylistEvent.ShowToast(
                        strings.playlistCreatedFormat(current.name.trim())
                    )
                )
                _events.send(CreatePlaylistEvent.CloseScreen)
            } catch (_: Throwable) {
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun updateState(
        name: String = _state.value.name,
        description: String = _state.value.description,
        pickedCover: Uri? = _state.value.pickedCover
    ) {
        val nameTrim = name
        val hasUnsaved = nameTrim.isNotBlank() || description.isNotBlank() || pickedCover != null
        val createEnabled = nameTrim.isNotBlank()
        _state.update {
            it.copy(
                name = nameTrim,
                description = description,
                pickedCover = pickedCover,
                hasUnsavedChanges = hasUnsaved,
                isCreateEnabled = createEnabled
            )
        }
    }

    interface Strings {
        val finishCreationTitle: String
        val unsavedDataMsg: String
        fun playlistCreatedFormat(name: String): String
    }
}