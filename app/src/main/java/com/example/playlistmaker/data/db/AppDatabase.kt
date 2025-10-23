package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.data.db.dao.PlaylistsDao
import com.example.playlistmaker.data.db.dao.TrackDao

@Database(version = 6, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTracksDao::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun playlistTracksDao(): PlaylistTracksDao
}