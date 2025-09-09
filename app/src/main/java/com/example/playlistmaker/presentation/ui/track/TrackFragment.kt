package com.example.playlistmaker.presentation.ui.track

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentTrackBinding
import com.example.playlistmaker.presentation.viewmodel.TrackViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackFragment : Fragment() {

    private var _binding: FragmentTrackBinding? = null
    private val binding get() = _binding!!

    private val args: TrackFragmentArgs by navArgs()
    private val viewModel: TrackViewModel by viewModel()

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            viewModel.updatePlaybackTime()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = args.track

        track.previewUrl?.let { url ->
            viewModel.preparePlayer(url)
        } ?: run {
            binding.playButton.isEnabled = false
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

            Glide.with(this@TrackFragment)
                .load(track.artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg"))
                .placeholder(R.drawable.placeholder)
                .into(albumCover)

            backButton.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            playButton.setOnClickListener {
                viewModel.togglePlayback()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
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

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateTimeRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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