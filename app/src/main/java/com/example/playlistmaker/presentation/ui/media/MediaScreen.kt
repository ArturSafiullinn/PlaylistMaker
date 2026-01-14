package com.example.playlistmaker.presentation.ui.media.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.models.UiTrack
import com.example.playlistmaker.presentation.ui.YsDisplay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaScreen(
    onOpenTrack: (UiTrack) -> Unit,
    onOpenPlaylist: (Long) -> Unit,
    onCreatePlaylist: () -> Unit
) {
    val tabs = listOf(
        stringResource(R.string.favorite_tracks),
        stringResource(R.string.playlists)
    )

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.settings_background_color))
            .padding(4.dp)
    ) {

        Text(
            text = stringResource(R.string.media_button),
            fontFamily = YsDisplay,
            fontWeight = FontWeight.W500,
            fontSize = 22.sp,
            color = colorResource(R.color.text_black),
            modifier = Modifier
                .padding(start = 12.dp, top = 10.dp, bottom = 14.dp)
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = colorResource(R.color.settings_background_color),
            contentColor = colorResource(R.color.text_black),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .padding(horizontal = 24.dp),
                    color = colorResource(R.color.text_black)
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    modifier = Modifier.padding(vertical = 16.dp),
                    selectedContentColor = colorResource(R.color.text_black),
                    unselectedContentColor = colorResource(R.color.text_black),
                    text = {
                        CompositionLocalProvider(
                            LocalTextStyle provides TextStyle(
                                fontFamily = YsDisplay,
                                fontWeight = FontWeight.W500,
                                fontSize = 14.sp
                            )
                        ) {
                            Text(text = title, maxLines = 1)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            when (page) {
                0 -> FavoritesTab(onOpenTrack = onOpenTrack)
                1 -> PlaylistsTab(
                    onOpenPlaylist = onOpenPlaylist,
                    onCreatePlaylist = onCreatePlaylist
                )
            }
        }
    }
}