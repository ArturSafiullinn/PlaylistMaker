// presentation/utils/TimeUtils.kt
package com.example.playlistmaker.presentation.utils

fun formatTime(millis: Int): String {
    val minutes = millis / 60000
    val seconds = (millis % 60000) / 1000
    return String.format("%02d:%02d", minutes, seconds)
}
