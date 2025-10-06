package com.example.playlistmaker.data

import com.example.playlistmaker.db.TrackEntity
import com.example.playlistmaker.domain.models.Track

class TrackDbConverter {

    fun map(track: Track): TrackEntity {
        return TrackEntity(track.trackId, track.trackName, track.artistName, track.trackTime, track.artworkUrl,
            track.collectionName, track.releaseDate, track.genre, track.country, track.previewUrl)
    }

    fun map(track: TrackEntity): Track {
        return Track(track.trackId, track.trackName, track.artistName, track.trackTime, track.artworkUrl,
            track.collectionName, track.releaseDate, track.genre, track.country, track.previewUrl)
    }
}