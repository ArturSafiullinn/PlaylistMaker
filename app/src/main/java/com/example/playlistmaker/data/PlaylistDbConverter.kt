package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist

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
}
