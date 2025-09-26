package com.example.playlistmaker.data.network.itunes

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.network.core.Response

class TracksSearchResponse : Response() {
    var results: List<TrackDto> = emptyList()
}
