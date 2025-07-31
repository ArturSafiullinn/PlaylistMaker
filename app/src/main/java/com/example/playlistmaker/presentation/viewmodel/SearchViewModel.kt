package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.SearchTracksInteractor
import com.example.playlistmaker.presentation.models.SearchScreenState


class SearchViewModel(
    private val searchTracksInteractor: SearchTracksInteractor
) : ViewModel() {

    private val _state = MutableLiveData<SearchScreenState>()
    val state: LiveData<SearchScreenState> = _state

    fun search(query: String) {
        _state.value = SearchScreenState.Loading

        fun search(query: String) {
            _state.value = SearchScreenState.Loading

            searchTracksInteractor.searchTracks(query) { result ->
                result
                    .onSuccess { tracks ->
                        _state.postValue(
                            if (tracks.isEmpty()) SearchScreenState.Empty
                            else SearchScreenState.Content(tracks)
                        )
                    }
                    .onFailure {
                        _state.postValue(SearchScreenState.Error)
                    }
            }
        }


    }

    fun clearResults() {
        _state.value = SearchScreenState.Empty
    }
}
