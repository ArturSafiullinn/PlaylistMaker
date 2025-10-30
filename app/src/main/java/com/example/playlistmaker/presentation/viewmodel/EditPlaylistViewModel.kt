package com.example.playlistmaker.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.presentation.ui.media.createPlaylist.CoverStorage
import com.example.playlistmaker.presentation.ui.media.createPlaylist.CreatePlaylistEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditPlaylistUiState(
    val playlistId: Long? = null,

    val name: String = "",
    val description: String = "",
    val initialCoverPath: String? = null,
    val coverPreview: Uri? = null,
    val isLoading: Boolean = false,

    val isCreateEnabled: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)

class EditPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val coverStorage: CoverStorage,
    private val strings: Strings
) : ViewModel() {

    private val _state = MutableStateFlow(EditPlaylistUiState())
    val state: StateFlow<EditPlaylistUiState> = _state.asStateFlow()

    private val _events = Channel<CreatePlaylistEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var initialName: String = ""
    private var initialDescription: String = ""
    private var initialCoverPath: String? = null

    /** Загрузка исходных данных плейлиста */
    fun load(playlistId: Long) {
        viewModelScope.launch {
            playlistsInteractor.observePlaylistById(playlistId).collect { p ->
                initialName = p.name
                initialDescription = p.description
                initialCoverPath = p.coverUri

                _state.update {
                    it.copy(
                        playlistId = p.playlistId,
                        name = p.name,
                        description = p.description,
                        initialCoverPath = p.coverUri,
                        coverPreview = null,
                        isLoading = false,
                        isCreateEnabled = p.name.isNotBlank(),
                        hasUnsavedChanges = false
                    )
                }
            }
        }
    }

    fun onNameChanged(value: String) = updateState(name = value)
    fun onDescriptionChanged(value: String) = updateState(description = value)

    fun onCoverPicked(uri: Uri?) {
        _state.update { s ->
            val changed = hasChanges(
                name = s.name,
                description = s.description,
                newCover = uri
            )
            s.copy(
                coverPreview = uri,
                hasUnsavedChanges = changed,
                isCreateEnabled = s.name.isNotBlank()
            )
        }
    }

    private fun updateState(
        name: String = _state.value.name,
        description: String = _state.value.description
    ) {
        val changed = hasChanges(
            name = name,
            description = description,
            newCover = _state.value.coverPreview
        )
        _state.update {
            it.copy(
                name = name,
                description = description,
                hasUnsavedChanges = changed,
                isCreateEnabled = name.isNotBlank()
            )
        }
    }

    private fun hasChanges(name: String, description: String, newCover: Uri?): Boolean {
        val coverChanged = newCover != null
        return (name != initialName) || (description != initialDescription) || coverChanged
    }

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
            _events.send(CreatePlaylistEvent.CloseScreen)
        }
    }

    fun saveChanges() {
        val s = _state.value
        val id = s.playlistId ?: return
        if (!s.isCreateEnabled || s.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val newCoverPath: String? =
                    s.coverPreview?.let { coverStorage.saveCover(it) } ?: initialCoverPath

                playlistsInteractor.updatePlaylistInfo(
                    playlistId = id,
                    name = s.name.trim(),
                    description = s.description.trim(),
                    coverUri = newCoverPath
                )

                _events.send(CreatePlaylistEvent.CloseScreen)
            } catch (_: Throwable) {
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    interface Strings {
        val finishCreationTitle: String
        val unsavedDataMsg: String
        fun playlistCreatedFormat(name: String): String
    }
}
