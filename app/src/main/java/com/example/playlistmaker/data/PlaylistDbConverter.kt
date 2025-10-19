package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist) : PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            name = playlist.name,
            description = playlist.description,
            playlistCover = playlist.playlistCover,
            playlistTracks = playlist.playlistTracks,
            playlistLength = playlist.playlistLength
        )
    }

    fun map(playlistEntity: PlaylistEntity) : Playlist {
        return Playlist(
            playlistId = playlistEntity.playlistId,
            name = playlistEntity.name,
            description = playlistEntity.description,
            playlistCover = playlistEntity.playlistCover,
            playlistTracks = playlistEntity.playlistTracks,
            playlistLength = playlistEntity.playlistLength
        )
    }
}