package com.example.playlistmaker.data.playlists

import com.example.playlistmaker.data.PlaylistDbConverter
import com.example.playlistmaker.data.db.PlaylistTrackEntity
import com.example.playlistmaker.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.data.db.dao.PlaylistsDao
import com.example.playlistmaker.data.util.IdsCsv
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao,
    private val playlistTracksDao: PlaylistTracksDao,
    private val converter: PlaylistDbConverter
) : PlaylistsRepository {

    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistsDao.observePlaylists().map { list -> list.map(converter::mapToDomain) }

    override suspend fun addToPlaylists(playlist: Playlist): Long {
        return playlistsDao.insertPlaylist(converter.mapToEntityForInsert(playlist))
    }

    override suspend fun deletePlaylistById(playlistId: Long) {
        playlistsDao.deletePlaylistById(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean {
        playlistTracksDao.insert(track.toPlaylistTrackEntity())

        val currentIds = IdsCsv.fromCsv(playlist.playlistTracks)
        if (currentIds.contains(track.trackId)) return false

        val newIds = currentIds + track.trackId
        playlistsDao.updateTracks(
            playlistId = playlist.playlistId,
            ids = IdsCsv.toCsv(newIds),
            count = newIds.size
        )
        return true
    }

    private fun Track.toPlaylistTrackEntity() = PlaylistTrackEntity(
        trackId = this.trackId,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTime ?: 0L,
        artworkUrl100 = this.previewUrl,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.genre,
        country = this.country,
        previewUrl = this.previewUrl
    )
}