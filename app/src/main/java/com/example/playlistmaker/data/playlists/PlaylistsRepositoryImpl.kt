package com.example.playlistmaker.data.playlists

import com.example.playlistmaker.data.PlaylistDbConverter
import com.example.playlistmaker.data.db.dao.PlaylistsDao
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val dao: PlaylistsDao,
    private val converter: PlaylistDbConverter
) : PlaylistsRepository {


    override fun getPlaylists(): Flow<List<Playlist>> =
        dao.observePlaylists().map { list -> list.map(converter::map) }

    override suspend fun addToPlaylists(playlist: Playlist) {
        dao.insertPlaylist(converter.map(playlist))
    }

    override suspend fun deletePlaylistById(playlistId: Long) {
        dao.deletePlaylistById(playlistId)
    }
}