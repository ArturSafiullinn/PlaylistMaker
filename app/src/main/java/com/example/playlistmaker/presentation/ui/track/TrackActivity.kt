package com.example.playlistmaker.presentation.ui.track

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityTrackBinding
import com.example.playlistmaker.presentation.viewmodel.TrackViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.presentation.models.UiTrack

class TrackActivity : AppCompatActivity() {

    private lateinit var track: UiTrack
    private lateinit var binding: ActivityTrackBinding
    private val viewModel: TrackViewModel by viewModel()
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            viewModel.updatePlaybackTime()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = intent.getParcelableExtra("track") ?: run {
            finish()
            return
        }

        track.previewUrl?.let { url ->
            viewModel.preparePlayer(url)
        }

        with(binding) {
            trackTitle.text = track.trackName
            trackArtist.text = track.artistName
            valueAlbum.text = track.collectionName
            valueYear.text = track.releaseDate
            valueGenre.text = track.genre
            valueCountry.text = track.country
            playbackTime.text = formatTime(0)
            playButton.isEnabled = false

            Glide.with(this@TrackActivity)
                .load(track.artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg"))
                .placeholder(R.drawable.placeholder)
                .into(albumCover)

            backButton.setOnClickListener { finish() }

            playButton.setOnClickListener {
                viewModel.togglePlayback()
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is TrackViewModel.UiState.Ready -> {
                        binding.playButton.isEnabled = true
                        binding.playButton.setImageResource(R.drawable.play_button)
                    }
                    is TrackViewModel.UiState.Playing -> {
                        binding.playButton.setImageResource(R.drawable.pause_button)
                        handler.post(updateTimeRunnable)
                    }
                    is TrackViewModel.UiState.Paused -> {
                        binding.playButton.setImageResource(R.drawable.play_button)
                        handler.removeCallbacks(updateTimeRunnable)
                    }
                    is TrackViewModel.UiState.Finished -> {
                        binding.playButton.setImageResource(R.drawable.play_button)
                        handler.removeCallbacks(updateTimeRunnable)
                        binding.playbackTime.text = formatTime(0)
                    }
                    is TrackViewModel.UiState.TimeUpdate -> {
                        binding.playbackTime.text = formatTime(state.millis)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        viewModel.releasePlayer()
    }

    private fun formatTime(millis: Int): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}