package com.example.playlistmaker.presentation.ui.track

import android.os.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.utils.Creator

class TrackActivity : AppCompatActivity() {

    private lateinit var playButton: ImageView
    private lateinit var playbackTimeTextView: TextView
    private lateinit var track: Track
    private lateinit var player: AudioPlayerInteractor

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (player.isPlaying()) {
                playbackTimeTextView.text = formatTime(player.getCurrentPosition())
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        track = intent.getParcelableExtra("track") ?: return

        player = Creator.provideAudioPlayerInteractor()

        findViewById<TextView>(R.id.track_title).text = track.trackName
        findViewById<TextView>(R.id.track_artist).text = track.artistName
        findViewById<TextView>(R.id.value_album).text = track.collectionName
        findViewById<TextView>(R.id.value_year).text = track.releaseDate
        findViewById<TextView>(R.id.value_genre).text = track.genre
        findViewById<TextView>(R.id.value_country).text = track.country
        playbackTimeTextView = findViewById(R.id.playback_time)
        playbackTimeTextView.text = formatTime(0)

        playButton = findViewById(R.id.play_button)
        playButton.isEnabled = false

        Glide.with(this)
            .load(track.artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .into(findViewById(R.id.album_cover))

        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }

        player.prepare(
            url = track.previewUrl,
            onReady = {
                playButton.isEnabled = true
                playButton.setImageResource(R.drawable.play_button)
            },
            onCompletion = {
                playButton.setImageResource(R.drawable.play_button)
                handler.removeCallbacks(updateTimeRunnable)
                playbackTimeTextView.text = formatTime(0)
            }
        )

        playButton.setOnClickListener {
            if (player.isPlaying()) {
                player.pause()
                playButton.setImageResource(R.drawable.play_button)
                handler.removeCallbacks(updateTimeRunnable)
            } else {
                player.play()
                playButton.setImageResource(R.drawable.pause_button)
                handler.post(updateTimeRunnable)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        player.release()
    }

    private fun formatTime(millis: Int): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
