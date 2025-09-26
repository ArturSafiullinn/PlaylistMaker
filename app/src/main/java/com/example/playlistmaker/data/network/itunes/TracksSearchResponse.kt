package com.example.playlistmaker.data.network.itunes

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.network.core.Response

data class TracksSearchResponse (val results: List<TrackDto>) : Response() {
}
