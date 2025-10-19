package com.example.playlistmaker.presentation.ui.media

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.playlistCover.setImageURI(uri)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.playlistCover.setOnClickListener{
            pickImage.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
        }

        binding.inputPlaylistName.doOnTextChanged{_,_,_,_ -> updateButtonState()}
        binding.inputPlaylistDescription.doOnTextChanged{_,_,_,_ -> updateButtonState()}

        binding.addPlaylistButton.setOnClickListener {
            val name = binding.inputPlaylistName.text?.toString().orEmpty().trim()
            val description = binding.inputPlaylistDescription.text?.toString().orEmpty().trim()
            val coverUri = selectedImageUri


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateButtonState() {
        val nameNotEmpty = binding.inputPlaylistName.text?.isNotBlank() == true
        val descriptionNotEmpty = binding.inputPlaylistDescription.text?.isNotBlank() == true
        val enabled = nameNotEmpty && descriptionNotEmpty
        binding.addPlaylistButton.isEnabled = enabled
        binding.addPlaylistButton.setBackgroundResource(
            if (enabled) R.drawable.add_playlist_button_active_background
            else R.drawable.add_playlist_button_inactive_background
        )
    }
}