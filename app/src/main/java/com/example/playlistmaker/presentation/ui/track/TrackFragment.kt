package com.example.playlistmaker.presentation.ui.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentTrackBinding
import com.example.playlistmaker.presentation.mappers.toDomain
import com.example.playlistmaker.presentation.ui.track.adapter.BsPlaylistsAdapter
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

        val uiTrack = args.track
        val domainTrack = uiTrack.toDomain()
        viewModel.bindTrack(domainTrack)

        if (uiTrack.previewUrl.isNullOrBlank()) {
            binding.playButton.isEnabled = false
        } else {
            viewModel.preparePlayer(uiTrack.previewUrl!!)
        }

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

            likeButton.setOnClickListener {
                viewModel.onLikeButtonClicked()
            }

            binding.newPlaylistButton.setOnClickListener {
                viewModel.onAddToPlaylistClicked()
            }

            val bottomSheet = binding.playlistsBottomSheet   // из разметки (см. пункт 3)
            val overlay = binding.overlay
            val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet).apply {
                state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
            }
            behavior.addBottomSheetCallback(object : com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottom: View, newState: Int) {
                    overlay.visibility = if (newState == com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
                }
                override fun onSlide(bottom: View, slideOffset: Float) {
                    overlay.alpha = kotlin.math.max(0f, slideOffset) // плавное затемнение
                }
            })
            overlay.setOnClickListener { behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN }

            val bsAdapter = BsPlaylistsAdapter { pl -> viewModel.onPickPlaylist(pl) }
            binding.bsPlaylistsRecycler.adapter = bsAdapter

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.playlistsFlow.collect { bsAdapter.submitList(it) }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.events.collect { e ->
                    when (e) {
                        is TrackViewModel.UiEvent.OpenBottomSheet ->
                            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
                        is TrackViewModel.UiEvent.CloseBottomSheet ->
                            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
                        is TrackViewModel.UiEvent.OpenCreatePlaylist ->
                            findNavController().navigate(R.id.action_trackFragment_to_createPlaylistFragment)
                        is TrackViewModel.UiEvent.ShowToast ->
                            android.widget.Toast.makeText(requireContext(), e.msg, android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }

            binding.newPlaylistButton.setOnClickListener { viewModel.onNewPlaylistClicked() }

        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            binding.playButton.isEnabled = state.isPlayButtonEnabled
            binding.playButton.setImageResource(state.buttonIcon)
            binding.playbackTime.text = state.progress
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) {isFavorite ->
            if (isFavorite) {
                binding.likeButton.setImageResource(R.drawable.add_to_favorite_active_button)
            }
            else {
                binding.likeButton.setImageResource(R.drawable.add_to_favorite_inactive_button)
            }
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