package com.example.playlistmaker.presentation.mappers

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.models.UiTrack

fun TrackDto.toDomain(): Track = Track(
    trackId = this.trackId ?: 0,
    trackName = this.trackName.orEmpty(),
    artistName = this.artistName.orEmpty(),
    trackTime = (this.trackTime ?: 0).toLong(),
    artworkUrl = this.artworkUrl100.orEmpty(),
    collectionName = this.collectionName,
    releaseDate = this.releaseDate,
    genre = this.primaryGenreName,
    country = this.country,
    previewUrl = this.previewUrl
)

fun Track.toUi(): UiTrack = UiTrack(
    id = this.trackId,
    trackName = this.trackName,
    artistName = this.artistName,
    trackTime = this.trackTime,
    artworkUrl = this.artworkUrl,
    collectionName = this.collectionName,
    releaseDate = this.releaseDate,
    genre = this.genre,
    country = this.country,
    previewUrl = this.previewUrl
)

fun UiTrack.toDomain(): Track = Track(
    trackId = this.id,
    trackName = this.trackName,
    artistName = this.artistName,
    trackTime = this.trackTime,
    artworkUrl = this.artworkUrl,
    collectionName = this.collectionName,
    releaseDate = this.releaseDate,
    genre = this.genre,
    country = this.country,
    previewUrl = this.previewUrl
)
