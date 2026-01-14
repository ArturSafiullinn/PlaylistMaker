package com.example.playlistmaker.presentation.ui.media.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.YsDisplay
import com.example.playlistmaker.presentation.viewmodel.PlaylistsState
import com.example.playlistmaker.presentation.viewmodel.PlaylistsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsTab(
    onOpenPlaylist: (Long) -> Unit,
    onCreatePlaylist: () -> Unit,
    viewModel: PlaylistsViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.observe()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.settings_background_color))
    ) {

        Button(
            onClick = onCreatePlaylist,
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.CenterHorizontally)
                .heightIn(min = 48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.text_black),
                contentColor = colorResource(R.color.text_white)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(54.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.add_playlists),
                fontFamily = YsDisplay,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        when (state) {
            is PlaylistsState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    EmptyPlaylists()
                }
            }

            is PlaylistsState.Content -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items, key = { it.playlistId }) { playlist ->
                        PlaylistCard(
                            name = playlist.name,
                            count = playlist.playlistLength,
                            coverUri = playlist.coverUri,
                            onClick = { onOpenPlaylist(playlist.playlistId) }
                        )
                    }
                }
            }
        }
    }
}