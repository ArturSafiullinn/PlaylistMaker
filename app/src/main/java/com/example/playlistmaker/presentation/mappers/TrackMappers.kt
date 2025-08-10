package com.example.playlistmaker.presentation.mappers

import com.example.playlistmaker.presentation.models.UiTrack
import com.example.playlistmaker.domain.models.Track as DomainTrack

fun DomainTrack.toUi(): UiTrack = UiTrack(
    id = this.trackId,
    trackName = this.trackName,
    artistName = this.artistName,
    artworkUrl = this.artworkUrl,
    previewUrl = this.previewUrl,
    collectionName = this.collectionName,
    releaseDate = this.releaseDate,
    genre = this.genre,
    country = this.country
)
