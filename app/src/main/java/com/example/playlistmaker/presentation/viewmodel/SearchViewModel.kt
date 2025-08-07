package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchTracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.models.SearchScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchTracksInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _state = MutableLiveData<SearchScreenState>()
    val state: LiveData<SearchScreenState> = _state

    private var searchJob: Job? = null

    fun searchDebounce(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(2000)
            if (query.isNotBlank()) search(query)
        }
    }

    fun search(query: String) {
        _state.postValue(SearchScreenState.Loading)

        searchInteractor.searchTracks(query) { result ->
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

    fun clearResults() {
        _state.value = SearchScreenState.Empty
    }

    fun loadHistory() {
        val history = historyInteractor.getHistory()
        _state.value = SearchScreenState.History(history)
    }

    fun addTrackToHistory(track: Track) {
        historyInteractor.addTrack(track)
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        loadHistory()
    }
}
