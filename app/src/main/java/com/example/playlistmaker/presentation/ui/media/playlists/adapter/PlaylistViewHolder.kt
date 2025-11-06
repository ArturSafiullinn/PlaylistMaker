package com.example.playlistmaker.presentation.ui.media.playlists.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import java.io.File

class PlaylistViewHolder(
    private val binding: ItemPlaylistBinding,
    private val onClick: (Playlist) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist) = with(binding) {
        title.text = item.name
        count.text = itemView.context.getString(R.string.playlist_tracks_count, item.playlistLength)
        val uri = item.coverUri.orEmpty()
        if (uri.isBlank()) {
            cover.setImageResource(R.drawable.album_placeholder)
        } else {
            Glide.with(cover)
                .load(File(uri))
                .placeholder(R.drawable.album_placeholder)
                .error(R.drawable.album_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(cover)
        }
        root.setOnClickListener { onClick(item) }
    }
}