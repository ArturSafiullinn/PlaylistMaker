package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {

    @Query("SELECT * FROM playlists ORDER BY playlistId DESC")
    fun observePlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(entity: PlaylistEntity) : Long

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

    @Query("UPDATE playlists SET playlistTracks = :ids, playlistLength = :count WHERE playlistId = :playlistId")
    suspend fun updateTracks(playlistId: Long, ids: String?, count: Int)

    @Query("SELECT * FROM playlists WHERE playlistId = :id")
    fun observePlaylistById(id: Long): Flow<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE playlistId = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity

    @Query("UPDATE playlists SET name = :name, description = :description, coverUri = :cover WHERE playlistId = :id")
    suspend fun updateInfo(id: Long, name: String, description: String, cover: String?)
}