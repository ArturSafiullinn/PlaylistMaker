package com.example.playlistmaker.presentation.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.databinding.PlaylistsFragmentBinding
import com.example.playlistmaker.presentation.viewmodel.PlaylistsState
import com.example.playlistmaker.presentation.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.presentation.ui.media.playlists.adapter.PlaylistsAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }

    private var _binding: PlaylistsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var adapter: PlaylistsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PlaylistsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistsAdapter { playlist ->
            // TODO: открыть детали плейлиста
        }

        binding.playlistsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecycler.adapter = adapter

        binding.newPlaylistButton.setOnClickListener {
            requireParentFragment()
                .findNavController()
                .navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }

        viewModel.state.onEach { render(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onStart() {
        super.onStart()
        viewModel.observe()
    }

    private fun render(state: PlaylistsState) = with(binding) {
        when (state) {
            is PlaylistsState.Empty -> {
                emptyGroup.visibility = View.VISIBLE
                playlistsRecycler.visibility = View.GONE
            }
            is PlaylistsState.Content -> {
                emptyGroup.visibility = View.GONE
                playlistsRecycler.visibility = View.VISIBLE
                adapter.submitList(state.items)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}