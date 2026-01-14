package com.example.playlistmaker.presentation.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.presentation.ui.media.compose.MediaScreen

class MediaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    MediaScreen(
                        onOpenTrack = { uiTrack ->
                            val action =
                                MediaFragmentDirections.actionMediaFragmentToTrackFragment(uiTrack)
                            requireParentFragment().findNavController().navigate(action)
                        },
                        onOpenPlaylist = { playlistId ->
                            val action =
                                MediaFragmentDirections.actionMediaFragmentToPlaylistDetailsFragment(playlistId)
                            requireParentFragment().findNavController().navigate(action)
                        },
                        onCreatePlaylist = {
                            requireParentFragment().findNavController().navigate(
                                com.example.playlistmaker.R.id.action_mediaFragment_to_createPlaylistFragment,
                                androidx.core.os.bundleOf("playlistId" to 0L)
                            )
                        }
                    )
                }
            }
        }
    }
}
