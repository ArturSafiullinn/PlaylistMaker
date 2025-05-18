package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)
        val track = intent.getParcelableExtra<Track>("track") ?: return

        findViewById<TextView>(R.id.track_title).text = track.trackName
        findViewById<TextView>(R.id.track_artist).text = track.artistName
        findViewById<TextView>(R.id.value_album).text = track.collectionName
        findViewById<TextView>(R.id.value_year).text = track.releaseDate
        findViewById<TextView>(R.id.value_genre).text = track.genre
        findViewById<TextView>(R.id.value_country).text = track.country
        findViewById<TextView>(R.id.playback_time).text = track.trackTime
        findViewById<TextView>(R.id.value_duration).text = track.trackTime

        val cover = findViewById<ImageView>(R.id.album_cover)
        val cornerRadiusDp = 8
        val density = this.resources.displayMetrics.density
        val cornerRadiusPx = (cornerRadiusDp * density).toInt()
        Glide.with(this)
            .load(track.artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.erro_placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusPx))
            .into(cover)

        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }
    }
}