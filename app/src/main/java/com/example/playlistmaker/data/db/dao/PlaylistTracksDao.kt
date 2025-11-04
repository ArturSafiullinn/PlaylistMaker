package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.PlaylistTrackEntity

@Dao
interface PlaylistTracksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(track: PlaylistTrackEntity): Long

    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:ids)")
    fun observeByIds(ids: List<Long>): kotlinx.coroutines.flow.Flow<List<PlaylistTrackEntity>>

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun deleteByTrackId(trackId: Long)
}