package com.example.playlistmaker.presentation.ui.search.adapter

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val trackArtist: TextView = itemView.findViewById(R.id.track_artist)
    private val trackDuration: TextView = itemView.findViewById(R.id.track_duration)
    private val artWorkUrl: ImageView = itemView.findViewById(R.id.album_image)

    fun bind(track: Track) {
        Log.d("PlaylistCover", "artworkUrl = ${track.artworkUrl}")

        trackName.text = track.trackName.trim()
        trackArtist.text = track.artistName.trim()
        trackDuration.text = formatDurationMs(track.trackTime)

        val cornerRadiusPx = (2 * itemView.resources.displayMetrics.density).toInt()
        Glide.with(itemView)
            .load(track.artworkUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error_placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusPx))
            .into(artWorkUrl)
    }

    private fun formatDurationMs(ms: Long): String {
        val totalSec = ms / 1000
        val minutes = totalSec / 60
        val seconds = totalSec % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
