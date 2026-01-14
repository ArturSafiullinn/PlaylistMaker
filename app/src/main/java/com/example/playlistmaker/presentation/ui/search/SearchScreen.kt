package com.example.playlistmaker.presentation.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.models.SearchScreenState
import com.example.playlistmaker.presentation.ui.YsDisplay

@Composable
fun SearchScreen(
    state: SearchScreenState,
    lastQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearchImeAction: (String) -> Unit,
    onClearQuery: () -> Unit,
    onRetry: () -> Unit,
    onClearHistory: () -> Unit,
    onTrackClick: (Track) -> Unit,
    onFocusEmptyQuery: () -> Unit,
) {
    val bg = colorResource(R.color.settings_background_color)

    var query by remember { mutableStateOf(lastQuery) }
    LaunchedEffect(lastQuery) {
        if (query != lastQuery) query = lastQuery
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(4.dp)
    ) {
        // TitleTextStyle: 22sp, bold, text_black, margins
        Text(
            text = stringResource(R.string.search_activity_title),
            fontFamily = YsDisplay,
            fontSize = 22.sp,
            fontWeight = FontWeight.W500,
            color = colorResource(R.color.text_black),
            modifier = Modifier.padding(start = 12.dp, top = 10.dp, bottom = 14.dp)
        )

        SearchBar(
            query = query,
            onQueryChanged = {
                query = it
                onQueryChanged(it)
            },
            onClear = {
                query = ""
                onClearQuery()
            },
            onDone = { onSearchImeAction(query) },
            onFocusEmptyQuery = onFocusEmptyQuery
        )

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is SearchScreenState.Loading -> CircularProgressIndicator()

                is SearchScreenState.Error -> ErrorBlock(onRetry = onRetry)

                is SearchScreenState.Empty -> EmptyBlock()

                is SearchScreenState.Content -> TrackList(
                    tracks = state.tracks,
                    onTrackClick = onTrackClick
                )

                is SearchScreenState.History -> {
                    if (state.tracks.isNotEmpty()) {
                        HistoryBlock(
                            tracks = state.tracks,
                            onTrackClick = onTrackClick,
                            onClearHistory = onClearHistory
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit,
    onDone: () -> Unit,
    onFocusEmptyQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val screenPadding = dimensionResource(R.dimen.screen_padding)
    val iconPadding = dimensionResource(R.dimen.search_bar_icon_padding)

    val bg = colorResource(R.color.light_gray)
    val textColor = colorResource(R.color.text_black)
    val editTextColor = colorResource(R.color.black)
    val hintColor = colorResource(R.color.gray)
    val iconTint = colorResource(R.color.gray)


    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = screenPadding)
            .background(bg, RoundedCornerShape(8.dp))
            .padding(all = iconPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.search),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(iconTint)
        )

        Spacer(Modifier.width(8.dp))

        BasicTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { fs ->
                    if (fs.isFocused && query.isEmpty()) onFocusEmptyQuery()
                },
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = YsDisplay,
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                color = editTextColor
            ),
            cursorBrush = SolidColor(textColor),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDone()
                    focusManager.clearFocus()
                }
            ),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_activity_title),
                        fontFamily = YsDisplay,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        color = hintColor
                    )
                }
                innerTextField()
            }
        )

        if (query.isNotEmpty()) {
            Spacer(Modifier.width(8.dp))
            Image(
                painter = painterResource(R.drawable.ic_clear),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onClear() },
                colorFilter = ColorFilter.tint(iconTint)
            )
        }
    }
}

@Composable
private fun HistoryBlock(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.you_searched),
            fontFamily = YsDisplay,
            fontSize = 19.sp,
            fontWeight = FontWeight.W500,
            color = colorResource(R.color.text_black),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp, bottom = 8.dp)
        )

        TrackList(
            tracks = tracks,
            onTrackClick = onTrackClick,
            modifier = Modifier.weight(1f)
        )

        SearchActionButton(
            text = stringResource(R.string.clear_history),
            onClick = onClearHistory,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, bottom = 16.dp)
        )
    }
}

@Composable
private fun EmptyBlock() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 32.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.nothing_found),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.nothing_found),
            fontFamily = YsDisplay,
            fontSize = 19.sp,
            fontWeight = FontWeight.W500,
            color = colorResource(R.color.text_black),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorBlock(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 32.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.no_internet),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = stringResource(R.string.no_internet),
            fontFamily = YsDisplay,
            fontSize = 19.sp,
            fontWeight = FontWeight.W500,
            color = colorResource(R.color.text_black),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp)
        )

        SearchActionButton(
            text = stringResource(R.string.refresh),
            onClick = onRetry,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun SearchActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(54.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.text_black),
            contentColor = colorResource(R.color.text_white)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp),
        modifier = modifier.heightIn(min = 48.dp)
    ) {
        Text(
            text = text,
            fontFamily = YsDisplay,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500
        )
    }
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(tracks) { track ->
            TrackRow(track = track, onClick = { onTrackClick(track) })
        }
    }
}

@Composable
private fun TrackRow(track: Track, onClick: () -> Unit) {
    val bg = colorResource(R.color.settings_background_color)
    val textBlack = colorResource(R.color.text_black)
    val gray1 = colorResource(R.color.gray1)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl,
            contentDescription = null,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp))
        )

        Spacer(Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = track.trackName,
                fontFamily = YsDisplay,
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                color = textBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = track.artistName,
                    fontFamily = YsDisplay,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    color = gray1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(5.dp))

                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .background(
                            color = colorResource(R.color.gray1),
                            shape = RoundedCornerShape(50)
                        )
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    text = formatMillis(track.trackTime),
                    fontFamily = YsDisplay,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    color = gray1
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Image(
            painter = painterResource(R.drawable.arrow_forward),
            contentDescription = null,
            colorFilter = ColorFilter.tint(gray1)
        )
    }
}

private fun formatMillis(ms: Long?): String {
    if (ms == null) return ""
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}