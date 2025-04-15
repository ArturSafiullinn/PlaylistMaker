package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView
    private val artistName: TextView
    private val trackTime: TextView
    private val artWorkUrl: ImageView
    init {
        trackName = itemView.findViewById(R.id.track_name)
        artistName = itemView.findViewById(R.id.track_artist)
        trackTime = itemView.findViewById(R.id.track_duration)
        artWorkUrl = itemView.findViewById(R.id.album_image)
    }
    fun bind (track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.erro_placeholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(artWorkUrl)
    }
}