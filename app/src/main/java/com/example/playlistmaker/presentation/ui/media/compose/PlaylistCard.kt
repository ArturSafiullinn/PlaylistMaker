package com.example.playlistmaker.presentation.ui.media.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.YsDisplay

@Composable
fun PlaylistCard(
    name: String,
    count: Int,
    coverUri: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.settings_background_color)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            val model = coverUri?.takeIf { it.isNotBlank() }

            if (model == null) {
                Image(
                    painter = painterResource(R.drawable.album_placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.album_placeholder),
                    error = painterResource(R.drawable.album_placeholder),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = YsDisplay,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                color = colorResource(R.color.text_black),
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = stringResource(R.string.playlist_tracks_count, count),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = YsDisplay,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                color = colorResource(R.color.text_black)
            )
        }
    }
}