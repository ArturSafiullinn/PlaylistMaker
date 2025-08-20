package com.example.playlistmaker.presentation.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FavoritesFragmentBinding
import com.example.playlistmaker.presentation.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoritesFragment : Fragment() {
    private var _binding: FavoritesFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FavoritesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}