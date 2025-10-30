package com.example.playlistmaker.presentation.ui.media.editPlaylist

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentEditPlaylistBinding
import com.example.playlistmaker.presentation.viewmodel.EditPlaylistViewModel
import com.example.playlistmaker.presentation.ui.media.createPlaylist.CreatePlaylistEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : Fragment() {

    private var _binding: FragmentEditPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditPlaylistViewModel by viewModel()

    private lateinit var backCallback: OnBackPressedCallback
    private val playlistId by lazy { requireArguments().getLong("playlistId") }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val corner = resources.getDimensionPixelSize(R.dimen.playlist_cover_corner_radius)
                Glide.with(this)
                    .load(uri)
                    .transform(CenterCrop(), RoundedCorners(corner))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ) = false

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.playlistCover.background = null
                            return false
                        }
                    })
                    .into(binding.playlistCover)
            } else {
                binding.playlistCover.setImageDrawable(null)
                binding.playlistCover.setBackgroundResource(R.drawable.new_playlist_image_background)
            }
            viewModel.onCoverPicked(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = viewModel.onBackPressed()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

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

        binding.addPlaylistButton.setOnClickListener { viewModel.saveChanges() }

        viewModel.state.onEach { s ->
            binding.inputPlaylistName.setTextIfChanged(s.name)
            binding.inputPlaylistDescription.setTextIfChanged(s.description)

            binding.addPlaylistButton.isEnabled = s.isCreateEnabled && !s.isLoading
            binding.addPlaylistButton.setBackgroundResource(
                if (s.isCreateEnabled) R.drawable.add_playlist_button_active_background
                else R.drawable.add_playlist_button_inactive_background
            )

            val corner = resources.getDimensionPixelSize(R.dimen.playlist_cover_corner_radius)
            when {
                s.coverPreview != null -> {
                    Glide.with(this)
                        .load(s.coverPreview)
                        .transform(CenterCrop(), RoundedCorners(corner))
                        .into(binding.playlistCover)
                    binding.playlistCover.background = null
                }
                !s.initialCoverPath.isNullOrBlank() -> {
                    Glide.with(this)
                        .load(File(s.initialCoverPath))
                        .transform(CenterCrop(), RoundedCorners(corner))
                        .into(binding.playlistCover)
                    binding.playlistCover.background = null
                }
                else -> {
                    binding.playlistCover.setImageDrawable(null)
                    binding.playlistCover.setBackgroundResource(R.drawable.new_playlist_image_background)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.events.onEach { e ->
            when (e) {
                is CreatePlaylistEvent.ShowDiscardDialog -> {
                    com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setTitle(e.title)
                        .setMessage(e.message)
                        .setNegativeButton(getString(R.string.cancel)) { d, _ -> d.dismiss() }
                        .setPositiveButton(getString(R.string.finish)) { _, _ ->
                            viewModel.confirmDiscardAndClose()
                        }
                        .show()
                }
                is CreatePlaylistEvent.ShowToast -> {
                    android.widget.Toast
                        .makeText(requireContext(), e.message, android.widget.Toast.LENGTH_SHORT)
                        .show()
                }
                is CreatePlaylistEvent.CloseScreen -> {
                    backCallback.isEnabled = false
                    findNavController().popBackStack()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.load(playlistId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun EditText.setTextIfChanged(newValue: String) {
    if (text?.toString() != newValue) setText(newValue)
}
