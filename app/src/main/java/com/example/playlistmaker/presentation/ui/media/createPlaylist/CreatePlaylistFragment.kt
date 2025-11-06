package com.example.playlistmaker.presentation.ui.media.createPlaylist

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.presentation.viewmodel.CreatePlaylistViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private val playlistId: Long by lazy {
        arguments?.getLong("playlistId") ?: 0L
    }

    private lateinit var backCallback: OnBackPressedCallback

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(resources.getDimensionPixelSize(R.dimen.playlist_cover_corner_radius))
                    )
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

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
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onBackPressed()
            }
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

        binding.addPlaylistButton.setOnClickListener { viewModel.savePlaylist() }

        viewModel.state.onEach { s ->
            with(binding) {
                val currentName = inputPlaylistName.text.toString()
                if (currentName != s.name) {
                    inputPlaylistName.setText(s.name)
                    inputPlaylistName.setSelection(s.name.length)
                }

                val currentDescription = inputPlaylistDescription.text.toString()
                if (currentDescription != s.description) {
                    inputPlaylistDescription.setText(s.description)
                    inputPlaylistDescription.setSelection(s.description.length)
                }

                val coverToLoad = s.coverToShow
                if (coverToLoad != null) {
                    Glide.with(this@CreatePlaylistFragment)
                        .load(coverToLoad)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(
                                resources.getDimensionPixelSize(
                                    R.dimen.playlist_cover_corner_radius
                                )
                            )
                        )
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
                                playlistCover.background = null
                                return false
                            }
                        })
                        .into(playlistCover)
                } else {
                    playlistCover.setImageDrawable(null)
                    playlistCover.setBackgroundResource(R.drawable.new_playlist_image_background)
                }

                addPlaylistButton.isEnabled = s.isCreateEnabled && !s.isLoading
                addPlaylistButton.setBackgroundResource(
                    if (s.isCreateEnabled) R.drawable.add_playlist_button_active_background
                    else R.drawable.add_playlist_button_inactive_background
                )
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

        if (playlistId == 0L) {
            bindingHeaderForCreateMode()
        } else {
            bindingHeaderForEditMode()
            viewModel.loadForEdit(playlistId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindingHeaderForCreateMode() {
        binding.apply {
            title.text = getString(R.string.new_playlist)
            addPlaylistButton.text = getString(R.string.add_playlist_button_text)
        }
    }

    private fun bindingHeaderForEditMode() {
        binding.apply {
            title.text = getString(R.string.edit)
            addPlaylistButton.text = getString(R.string.save)
        }
    }
}