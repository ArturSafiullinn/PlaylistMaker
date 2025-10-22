package com.example.playlistmaker.presentation.ui.media.createPlaylist

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.presentation.viewmodel.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                binding.playlistCover.setImageURI(uri)
            }
            viewModel.onCoverPicked(uri)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onBackPressed()
        }

        binding.backButton.setOnClickListener { viewModel.onBackPressed() }

        binding.playlistCover.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.inputPlaylistName.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameChanged(text?.toString().orEmpty())
        }
        binding.inputPlaylistDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.onDescriptionChanged(text?.toString().orEmpty())
        }

        binding.addPlaylistButton.setOnClickListener { viewModel.createPlaylist() }

        viewModel.state.onEach { s ->
            binding.addPlaylistButton.isEnabled = s.isCreateEnabled
            binding.addPlaylistButton.setBackgroundResource(
                if (s.isCreateEnabled) R.drawable.add_playlist_button_active_background
                else R.drawable.add_playlist_button_inactive_background
            )
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.events.onEach { e ->
            when (e) {
                is CreatePlaylistEvent.ShowDiscardDialog -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(e.title)
                        .setMessage(e.message)
                        .setNegativeButton(getString(R.string.cancel)) { d, _ -> d.dismiss() }
                        .setPositiveButton(getString(R.string.finish)) { _, _ ->
                            viewModel.confirmDiscardAndClose()
                        }
                        .show()
                }
                is CreatePlaylistEvent.ShowToast -> {
                    android.widget.Toast.makeText(requireContext(), e.message, android.widget.Toast.LENGTH_SHORT).show()
                }
                is CreatePlaylistEvent.CloseScreen -> {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
