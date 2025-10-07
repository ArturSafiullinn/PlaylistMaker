package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.domain.models.Track

class TrackDbConverter {

    fun map(track: Track, now: Long = System.currentTimeMillis()): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl = track.artworkUrl,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            genre = track.genre,
            country = track.country,
            previewUrl = track.previewUrl,
            addedAt = now
        )
    }

    fun map(entity: TrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl = entity.artworkUrl,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            genre = entity.genre,
            country = entity.country,
            previewUrl = entity.previewUrl
        )
    }
}