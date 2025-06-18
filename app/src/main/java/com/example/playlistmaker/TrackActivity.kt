package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class TrackActivity : AppCompatActivity() {
    private lateinit var playButton: ImageView
    private var mediaPlayer = MediaPlayer()
    private lateinit var trackPreviewUrl: String
    private lateinit var playbackTimeTextView: TextView

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    private val handler = Handler(Looper.getMainLooper())
    private val updatePlaybackTimeRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                playbackTimeTextView.text = formatTime(mediaPlayer.currentPosition)
                handler.postDelayed(this, 1000)
            }
        }
    }

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
        playbackTimeTextView = findViewById(R.id.playback_time)
        playbackTimeTextView.text = formatTime(0)
        trackPreviewUrl = track.previewUrl

        playButton = findViewById(R.id.play_button)
        playButton.isEnabled = false

        preparePlayer()

        playButton.setOnClickListener {
            when (playerState) {
                STATE_PLAYING -> {
                    mediaPlayer.pause()
                    playerState = STATE_PAUSED
                    playButton.setImageResource(R.drawable.play_button)
                    handler.removeCallbacks(updatePlaybackTimeRunnable)
                }
                STATE_PREPARED, STATE_PAUSED -> {
                    mediaPlayer.start()
                    playerState = STATE_PLAYING
                    playButton.setImageResource(R.drawable.pause_button)
                    handler.post(updatePlaybackTimeRunnable)
                }
            }
        }

        Glide.with(this)
            .load(track.artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .into(findViewById(R.id.album_cover))

        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(trackPreviewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
            playButton.setImageResource(R.drawable.play_button)
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            playButton.setImageResource(R.drawable.play_button)
            handler.removeCallbacks(updatePlaybackTimeRunnable)
            playbackTimeTextView.text = formatTime(0)
        }
    }

    private fun formatTime(millis: Int): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updatePlaybackTimeRunnable)
        mediaPlayer.release()
    }
}
