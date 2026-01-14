package com.example.playlistmaker.presentation.ui.search

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.mappers.toUi
import com.example.playlistmaker.presentation.models.SearchScreenState
import com.example.playlistmaker.presentation.models.UiTrack
import com.example.playlistmaker.presentation.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.presentation.utils.debounce

class SearchFragment : Fragment(R.layout.fragment_search) {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var trackClickDebounce: (Track) -> Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val composeView = view as ComposeView
        composeView.setContent {

            val screenState by viewModel.state.observeAsState(initial = SearchScreenState.History(emptyList()))

            trackClickDebounce = debounce(
                delayMillis = CLICK_DEBOUNCE_DELAY,
                coroutineScope = viewLifecycleOwner.lifecycleScope,
                useLastParam = false
            ) { track ->
                viewModel.addTrackToHistory(track)
                openTrackDetails(track)
            }

            SearchScreen(
                state = screenState,
                lastQuery = viewModel.getLastQuery(),
                onQueryChanged = { text ->
                    viewModel.onQueryChanged(text)
                    if (text.isEmpty()) {
                        viewModel.clearResults()
                        viewModel.loadHistory()
                    } else {
                        viewModel.searchDebounce(text)
                    }
                },
                onSearchImeAction = { query ->
                    if (query.isNotEmpty()) {
                        viewModel.cancelDebounce()
                        viewModel.search(query)
                    }
                },
                onClearQuery = {
                    viewModel.clearResults()
                    viewModel.loadHistory()
                },
                onRetry = {
                    val query = viewModel.getLastQuery()
                    if (query.isNotEmpty()) {
                        viewModel.cancelDebounce()
                        viewModel.search(query)
                    }
                },
                onClearHistory = { viewModel.clearHistory() },
                onTrackClick = { trackClickDebounce(it) },
                onFocusEmptyQuery = {
                    if (viewModel.getLastQuery().isEmpty() && screenState !is SearchScreenState.Content) {
                        viewModel.loadHistory()
                    }
                }
            )
        }

        if (viewModel.state.value == null) { // если это LiveData
            viewModel.loadHistory()
        }
    }

    private fun openTrackDetails(track: Track) {
        val uiTrack: UiTrack = track.toUi()
        val action = SearchFragmentDirections.actionSearchFragmentToTrackFragment(uiTrack)
        findNavController().navigate(action)
    }
}