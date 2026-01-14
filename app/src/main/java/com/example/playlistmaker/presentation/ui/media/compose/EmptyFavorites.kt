package com.example.playlistmaker.presentation.ui.media.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.YsDisplay

@Composable
fun EmptyFavorites() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 106.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.nothing_found),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = stringResource(R.string.empty_favorites),
            modifier = Modifier.padding(top = 16.dp),
            fontFamily = YsDisplay,
            fontWeight = FontWeight.W500,
            fontSize = 19.sp,
            color = colorResource(R.color.text_black)
        )
    }
}