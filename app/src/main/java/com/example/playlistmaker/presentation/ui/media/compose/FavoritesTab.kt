package com.example.playlistmaker.presentation.ui.media.compose

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.mappers.toUi
import com.example.playlistmaker.presentation.models.FavoritesScreenState
import com.example.playlistmaker.presentation.models.UiTrack
import com.example.playlistmaker.presentation.viewmodel.FavoritesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesTab(
    onOpenTrack: (UiTrack) -> Unit,
    viewModel: FavoritesViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.observeAsState(FavoritesScreenState.Empty)

    LifecycleResumeEffect(Unit) {
        viewModel.getFavorites()
        onPauseOrDispose { }
    }

    when (val s = state) {
        is FavoritesScreenState.Empty -> EmptyFavorites()

        is FavoritesScreenState.Content -> {
            val onTrackClick = rememberClickDebounce { track ->
                onOpenTrack(track.toUi())
            }

            TracksList(
                tracks = s.tracks,
                onClick = onTrackClick
            )
        }
    }
}

@Composable
private fun rememberClickDebounce(
    delayMs: Long = 300L,
    onClick: (Track) -> Unit
): (Track) -> Unit {
    val scope = rememberCoroutineScope()
    var locked by remember { mutableStateOf(false) }

    return { track ->
        if (!locked) {
            locked = true
            onClick(track)
            scope.launch {
                delay(delayMs)
                locked = false
            }
        }
    }
}

