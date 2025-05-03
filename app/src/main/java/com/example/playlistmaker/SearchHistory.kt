package com.example.playlistmaker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SearchHistory {
    private const val PREF_NAME = "search_history"
    private const val KEY_TRACK_HISTORY = "track_history"
    private const val MAX_HISTORY_SIZE = 10

    fun getHistory(context: Context): ArrayList<Track> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_TRACK_HISTORY, null)
        return if (!json.isNullOrEmpty()) {
            val rawHistory: ArrayList<Track> = Gson().fromJson(json, object : TypeToken<ArrayList<Track>>() {}.type)
            ArrayList(
                rawHistory.map {
                    it.copy(
                        trackName = it.trackName.trim(),
                        artistName = it.artistName.trim(),
                        trackTime = it.trackTime.trim(),
                        artworkUrl = it.artworkUrl.trim()
                    )
                }
            )
        } else {
            arrayListOf()
        }
    }


    fun saveHistory(context: Context, history: ArrayList<Track>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(history)
        editor.putString(KEY_TRACK_HISTORY, json)
        editor.apply()
    }

    fun addTrack(context: Context, track: Track) {
        val cleanedTrack = track.copy(
            trackName = track.trackName.trim(),
            artistName = track.artistName.trim(),
            trackTime = track.trackTime.trim(),
            artworkUrl = track.artworkUrl.trim()
        )
        val history = getHistory(context)
        history.removeAll { it.trackId == cleanedTrack.trackId }
        history.add(0, cleanedTrack)
        if (history.size > MAX_HISTORY_SIZE) history.removeLast()
        saveHistory(context, history)
    }


    fun clearHistory(context: Context) {
        saveHistory(context, arrayListOf())
    }
}
