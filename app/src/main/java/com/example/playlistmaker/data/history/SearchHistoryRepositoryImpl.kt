package com.example.playlistmaker.data.history

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val prefs: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {


    companion object {
        private const val HISTORY_KEY = "track_history"
        private const val MAX_HISTORY = 10
    }

    override fun getHistory(): List<Track> {
        val json = prefs.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_HISTORY) {
            history.removeLast()
        }
        saveHistory(history)
    }

    override fun clearHistory() {
        prefs.edit().remove(HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        prefs.edit().putString(HISTORY_KEY, json).apply()
    }
}