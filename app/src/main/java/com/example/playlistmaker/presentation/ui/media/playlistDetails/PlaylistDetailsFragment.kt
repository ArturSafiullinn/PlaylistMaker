package com.example.playlistmaker.presentation.ui.media.playlistDetails

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.mappers.toUi
import com.example.playlistmaker.presentation.ui.media.playlists.PlaylistDetailsViewModel
import com.example.playlistmaker.presentation.ui.search.adapter.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import android.widget.Toast

class PlaylistDetailsFragment : Fragment(), PlaylistMenuBottomSheet.Callbacks {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistDetailsFragmentArgs by navArgs()
    private val viewModel: PlaylistDetailsViewModel by viewModel()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            isHideable = false
            skipCollapsed = false
            isDraggable = true
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.isFitToContents = false

        binding.header.bringToFront()

        ViewCompat.setOnApplyWindowInsetsListener(binding.playlistDetailsRoot) { _, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            bottomSheetBehavior.expandedOffset = topInset
            insets
        }

        binding.playlistDetailsRoot.doOnLayout {
            val rootH = binding.playlistDetailsRoot.height
            val headerBottom = binding.header.bottom
            bottomSheetBehavior.peekHeight = (rootH - headerBottom).coerceAtLeast(0)
        }

        adapter = TrackAdapter(
            mutableListOf(),
            onItemClick = { track -> openPlayer(track) },
            onItemLongClick = { track -> confirmRemove(track) }
        )
        binding.tracksRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PlaylistDetailsFragment.adapter
        }


        binding.backButton.setOnClickListener {
            Toast.makeText(requireContext(), "BACK CLICK", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.shareButton.setOnClickListener {
            val shareText = viewModel.buildShareText()
            if (shareText.isNotBlank()) shareText(shareText)
        }

        binding.menuButton.setOnClickListener { showMenuBottomSheet() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    adapter.updateData(state.tracks)
                    render(state)
                }
            }
        }

        viewModel.load(args.playlistId)

        binding.shareButton.setOnClickListener {
            trySharePlaylist()
        }
    }

    private fun showMenuBottomSheet() {
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        val s = viewModel.state.value
        val pl = s.playlist ?: return
        val meta = getString(
            R.string.playlist_meta_template,
            formatDurationMinutes(s.totalDurationMinutes),
            resources.getQuantityString(R.plurals.tracks_count, s.tracks.size, s.tracks.size)
        )

        PlaylistMenuBottomSheet
            .newInstance(pl.playlistId, pl.name, pl.coverUri, meta)
            .show(childFragmentManager, PlaylistMenuBottomSheet.TAG)
    }

    private fun render(state: PlaylistDetailsUiState) = with(binding) {
        val corner = resources.getDimensionPixelSize(R.dimen.corner_radius_8)
        val coverPath = state.playlist?.coverUri
        if (!coverPath.isNullOrBlank()) {
            Glide.with(cover)
                .load(File(coverPath))
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(RoundedCorners(corner))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(cover)
        } else {
            cover.setImageResource(R.drawable.placeholder)
        }

        title.text = state.playlist?.name
        description.apply {
            text = state.playlist?.description.orEmpty()
            visibility = if (state.playlist?.description.isNullOrBlank()) View.GONE else View.VISIBLE
        }
        meta.text = getString(
            R.string.playlist_meta_template,
            formatDurationMinutes(state.totalDurationMinutes),
            resources.getQuantityString(
                R.plurals.tracks_count, state.tracks.size, state.tracks.size
            )
        )

        playlistDetailsRoot.doOnLayout {
            val rootH = playlistDetailsRoot.height
            val headerBottom = header.bottom
            bottomSheetBehavior.peekHeight = (rootH - headerBottom).coerceAtLeast(0)
        }
    }

    private fun formatDurationMinutes(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) getString(R.string.hh_mm_template, hours, minutes)
        else resources.getQuantityString(R.plurals.minutes_short, minutes, minutes)
    }

    private fun openPlayer(track: Track) {
        val uiTrack = track.toUi()
        findNavController().navigate(
            R.id.trackFragment,
            bundleOf("track" to uiTrack)
        )
    }

    private fun confirmRemove(track: Track) {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.remove_track_question))
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.removeTrack(track)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share)))
    }

    override fun onShareFromMenu() {
        val shareText = viewModel.buildShareText()
        if (!shareText.isNullOrBlank()) shareText(shareText)
        trySharePlaylist()
    }

    override fun onDeleteFromMenu() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_playlist)
            .setMessage(R.string.remove_playlist_question)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deletePlaylist()
                findNavController().navigateUp()
            }
            .show()
    }

    override fun onMenuDismissed() {
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun trySharePlaylist() {
        val shareText = viewModel.buildShareText()
        if (shareText.isBlank()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_tracks_to_share),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            shareText(shareText)
        }
    }
}