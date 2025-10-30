package com.example.playlistmaker.presentation.ui.media.playlistDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.BottomsheetPlaylistMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class PlaylistMenuBottomSheet : BottomSheetDialogFragment() {

    interface Callbacks {
        fun onShareFromMenu()
        fun onDeleteFromMenu()
    }

    private var _binding: BottomsheetPlaylistMenuBinding? = null
    private val binding get() = _binding!!
    private val corner by lazy { resources.getDimensionPixelSize(R.dimen.corner_radius_8) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetPlaylistMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = true

        val title = requireArguments().getString(ARG_TITLE).orEmpty()
        val cover = requireArguments().getString(ARG_COVER)
        val meta  = requireArguments().getString(ARG_META).orEmpty()

        binding.playlistTitle.text = title
        binding.playlistTrackCount.text = meta

        if (!cover.isNullOrBlank()) {
            Glide.with(binding.playlistCover)
                .load(File(cover))
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(RoundedCorners(corner))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.playlistCover)
        } else {
            binding.playlistCover.setImageResource(R.drawable.placeholder)
        }

        binding.actionShare.setOnClickListener {
            (parentFragment as? Callbacks)?.onShareFromMenu()
            dismiss()
        }
        binding.actionEdit.setOnClickListener {
            // по заданию — ничего
            dismiss()
        }
        binding.actionDelete.setOnClickListener {
            (parentFragment as? Callbacks)?.onDeleteFromMenu()
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "PlaylistMenuBottomSheet"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_COVER = "arg_cover"
        private const val ARG_META  = "arg_meta"

        fun newInstance(title: String, cover: String?, meta: String): PlaylistMenuBottomSheet =
            PlaylistMenuBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_COVER, cover)
                    putString(ARG_META,  meta)
                }
            }
    }
}