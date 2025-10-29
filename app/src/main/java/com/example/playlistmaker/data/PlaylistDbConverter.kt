package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistTrackEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track

class PlaylistDbConverter {

    fun mapToEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            name = playlist.name,
            description = playlist.description,
            coverUri = playlist.coverUri,
            playlistTracks = playlist.playlistTracks,
            playlistLength = playlist.playlistLength
        )
    }

    fun mapToEntityForInsert(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = 0L,
            name = playlist.name,
            description = playlist.description,
            coverUri = playlist.coverUri,
            playlistTracks = playlist.playlistTracks,
            playlistLength = playlist.playlistLength
        )
    }

    fun mapToDomain(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlistEntity.playlistId,
            name = playlistEntity.name,
            description = playlistEntity.description,
            coverUri = playlistEntity.coverUri,
            playlistTracks = playlistEntity.playlistTracks,
            playlistLength = playlistEntity.playlistLength
        )
    }

    fun mapTrackEntityToDomain(entity: PlaylistTrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTime = entity.trackTimeMillis,
            artworkUrl = entity.artworkUrl100.orEmpty(),
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            genre = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl
        )
    }

    fun mapTrackDomainToEntity(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTime ?: 0L,
            artworkUrl100 = track.artworkUrl,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.genre,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}
