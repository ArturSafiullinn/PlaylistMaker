package com.example.playlistmaker.presentation.ui.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentTrackBinding
import com.example.playlistmaker.presentation.mappers.toDomain
import com.example.playlistmaker.presentation.ui.track.adapter.BsPlaylistsAdapter
import com.example.playlistmaker.presentation.viewmodel.TrackViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackFragment : Fragment() {

    private var _binding: FragmentTrackBinding? = null
    private val binding get() = _binding!!

    private val args: TrackFragmentArgs by navArgs()
    private val viewModel: TrackViewModel by viewModel()

    companion object {
        private const val PEEK_SCRIM_ALPHA = 0.35f
        private const val EXPANDED_SCRIM_ALPHA = 0.75f
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

        val uiTrack = args.track
        val domainTrack = uiTrack.toDomain()
        viewModel.bindTrack(domainTrack)

        if (uiTrack.previewUrl.isNullOrBlank()) {
            binding.playButton.isEnabled = false
        } else {
            viewModel.preparePlayer(uiTrack.previewUrl!!)
        }

        with(binding) {
            trackTitle.text = uiTrack.trackName
            trackArtist.text = uiTrack.artistName
            valueAlbum.text = uiTrack.collectionName
            valueYear.text = uiTrack.releaseDate
            valueGenre.text = uiTrack.genre
            valueCountry.text = uiTrack.country
            playbackTime.text = "00:00"
            playButton.isEnabled = false

            Glide.with(this@TrackFragment)
                .load(uiTrack.artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg"))
                .placeholder(R.drawable.placeholder)
                .into(albumCover)

            backButton.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            playButton.setOnClickListener { viewModel.onPlayButtonClicked() }
            likeButton.setOnClickListener { viewModel.onLikeButtonClicked() }
        }

        val bottomSheet = binding.playlistsBottomSheet
        val overlay = binding.overlay

        overlay.isClickable = true

        val density = resources.displayMetrics.density
        ViewCompat.setElevation(bottomSheet, 16f * density)
        ViewCompat.setElevation(overlay, 8f * density)

        val behavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            skipCollapsed = false
            isDraggable = true
            peekHeight = (300 * density).toInt()
        }

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottom: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        overlay.visibility = View.VISIBLE
                        overlay.alpha = PEEK_SCRIM_ALPHA
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        overlay.visibility = View.VISIBLE
                        overlay.alpha = EXPANDED_SCRIM_ALPHA
                    }
                    else -> Unit
                }
            }

            override fun onSlide(bottom: View, slideOffset: Float) {
                if (overlay.visibility != View.VISIBLE) overlay.visibility = View.VISIBLE
                val t = slideOffset.coerceIn(0f, 1f)
                overlay.alpha =
                    PEEK_SCRIM_ALPHA + (EXPANDED_SCRIM_ALPHA - PEEK_SCRIM_ALPHA) * t
            }
        })

        overlay.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        val bsAdapter = BsPlaylistsAdapter { playlist ->
            viewModel.onPickPlaylist(playlist)
        }
        binding.bsPlaylistsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.bsPlaylistsRecycler.adapter = bsAdapter

        binding.addButton.setOnClickListener {
            viewModel.onAddToPlaylistClicked()
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED // ← изменено
        }

        binding.newPlaylistButton.setOnClickListener {
            viewModel.onNewPlaylistClicked()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.playlistsFlow.collect { list ->
                        bsAdapter.submitList(list)
                    }
                }
                launch {
                    viewModel.events.collect { e ->
                        when (e) {
                            is TrackViewModel.UiEvent.OpenBottomSheet ->
                                behavior.state = BottomSheetBehavior.STATE_COLLAPSED // ← изменено
                            is TrackViewModel.UiEvent.CloseBottomSheet ->
                                behavior.state = BottomSheetBehavior.STATE_HIDDEN
                            is TrackViewModel.UiEvent.OpenCreatePlaylist ->
                                findNavController().navigate(R.id.action_trackFragment_to_createPlaylistFragment)
                            is TrackViewModel.UiEvent.ShowToast ->
                                Toast.makeText(requireContext(), e.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            binding.playButton.isEnabled = state.isPlayButtonEnabled
            binding.playButton.setImageResource(state.buttonIcon)
            binding.playbackTime.text = state.progress
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.likeButton.setImageResource(
                if (isFavorite) R.drawable.add_to_favorite_active_button
                else R.drawable.add_to_favorite_inactive_button
            )
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