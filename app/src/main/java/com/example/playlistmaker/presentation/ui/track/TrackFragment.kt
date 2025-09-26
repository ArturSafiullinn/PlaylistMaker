package com.example.playlistmaker.presentation.ui.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentTrackBinding
import com.example.playlistmaker.presentation.viewmodel.TrackViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackFragment : Fragment() {

    private var _binding: FragmentTrackBinding? = null
    private val binding get() = _binding!!

    private val args: TrackFragmentArgs by navArgs()
    private val viewModel: TrackViewModel by viewModel()

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

        if (track.previewUrl.isNullOrBlank()) {
            binding.playButton.isEnabled = false
        } else {
            viewModel.preparePlayer(track.previewUrl!!)
        }

        with(binding) {
            trackTitle.text = track.trackName
            trackArtist.text = track.artistName
            valueAlbum.text = track.collectionName
            valueYear.text = track.releaseDate
            valueGenre.text = track.genre
            valueCountry.text = track.country
            playbackTime.text = "00:00"
            playButton.isEnabled = false

            Glide.with(this@TrackFragment)
                .load(track.artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg"))
                .placeholder(R.drawable.placeholder)
                .into(albumCover)

            backButton.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            playButton.setOnClickListener {
                viewModel.onPlayButtonClicked()
            }
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            binding.playButton.isEnabled = state.isPlayButtonEnabled
            binding.playButton.setImageResource(state.buttonIcon)
            binding.playbackTime.text = state.progress
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}