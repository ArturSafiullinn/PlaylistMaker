package com.example.playlistmaker.presentation.ui.media.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.YsDisplay

@Composable
fun TrackRow(
    track: Track,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // album_image 45dp
        AsyncImage(
            model = track.artworkUrl,
            contentDescription = null,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
            contentScale = ContentScale.Crop,
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = YsDisplay,
                fontWeight = FontWeight.W400,
                fontSize = 16.sp,
                color = colorResource(R.color.text_black)
            )

            Spacer(Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.artistName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = YsDisplay,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = colorResource(R.color.gray1),
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    text = "•",
                    fontFamily = YsDisplay,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = colorResource(R.color.gray1)
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    text = track.trackTime.toDurationString(),
                    fontFamily = YsDisplay,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = colorResource(R.color.gray1)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Image(
            painter = painterResource(R.drawable.arrow_forward),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorResource(R.color.gray1)),
            modifier = Modifier.fillMaxHeight()
        )
    }
}

private fun Long.toDurationString(): String {
    val totalSec = (this / 1000).toInt()
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}

@Composable
fun TracksList(
    tracks: List<Track>,
    onClick: (Track) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tracks, key = { it.trackId }) { track ->
            TrackRow(
                track = track,
                onClick = { onClick(track) }
            )
        }
    }
}