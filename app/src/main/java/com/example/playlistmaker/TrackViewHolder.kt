package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView
    private val trackArtist: TextView
    private val trackDurattion: TextView
    private val artWorkUrl: ImageView
    init {
        trackName = itemView.findViewById(R.id.track_name)
        trackArtist = itemView.findViewById(R.id.track_artist)
        trackDurattion = itemView.findViewById(R.id.track_duration)
        artWorkUrl = itemView.findViewById(R.id.album_image)
    }
    fun bind (track: Track) {
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackDurattion.text = track.trackTime
        val cornerRadiusDp = 2
        val density = itemView.resources.displayMetrics.density
        val cornerRadiusPx = (cornerRadiusDp * density).toInt()
        Glide.with(itemView)
            .load(track.artworkUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.erro_placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusPx))
            .into(artWorkUrl)
    }
}