package com.example.playlistmaker.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.mappers.toUi
import com.example.playlistmaker.presentation.models.FavoritesScreenState
import com.example.playlistmaker.presentation.models.UiTrack
import com.example.playlistmaker.presentation.ui.media.MediaFragmentDirections
import com.example.playlistmaker.presentation.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.presentation.utils.debounce
import com.example.playlistmaker.presentation.viewmodel.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter

    private lateinit var trackClickDebounce: (Track) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackClickDebounce = debounce(
            delayMillis = CLICK_DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false
        ) { track ->
            openTrackDetails(track)
        }

        initAdapters()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesScreenState.Empty -> {
                    binding.favoritesEmpty.visibility = View.VISIBLE
                    binding.favoritesRecycler.visibility = View.GONE
                }
                is FavoritesScreenState.Content -> {
                    binding.favoritesEmpty.visibility = View.GONE
                    binding.favoritesRecycler.visibility = View.VISIBLE
                    trackAdapter.updateData(state.tracks)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAdapters() {
        trackAdapter = TrackAdapter(
            mutableListOf(),
            onItemClick = { track ->
                trackClickDebounce(track)
                },
            {})
        binding.favoritesRecycler.adapter = trackAdapter
    }

    private fun openTrackDetails(track: Track) {
        val uiTrack: UiTrack = track.toUi()
        val action = MediaFragmentDirections.actionMediaFragmentToTrackFragment(uiTrack)
        val navController = NavHostFragment.findNavController(requireParentFragment())
        navController.navigate(action)
    }
}