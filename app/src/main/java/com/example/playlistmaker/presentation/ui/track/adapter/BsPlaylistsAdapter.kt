package com.example.playlistmaker.presentation.ui.track.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R

class BsPlaylistsAdapter(
    private val onClick: (com.example.playlistmaker.domain.models.Playlist) -> Unit
) : androidx.recyclerview.widget.ListAdapter<com.example.playlistmaker.domain.models.Playlist, BsVH>(
    object : androidx.recyclerview.widget.DiffUtil.ItemCallback<com.example.playlistmaker.domain.models.Playlist>() {
        override fun areItemsTheSame(o: com.example.playlistmaker.domain.models.Playlist, n: com.example.playlistmaker.domain.models.Playlist) = o.playlistId == n.playlistId
        override fun areContentsTheSame(o: com.example.playlistmaker.domain.models.Playlist, n: com.example.playlistmaker.domain.models.Playlist) = o == n
    }
) {
    override fun onCreateViewHolder(p: ViewGroup, vt: Int): BsVH {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_bs_playlist, p, false)
        return BsVH(v, onClick)
    }
    override fun onBindViewHolder(h: BsVH, pos: Int) = h.bind(getItem(pos))
}

class BsVH(v: View, private val onClick: (com.example.playlistmaker.domain.models.Playlist) -> Unit) :
    RecyclerView.ViewHolder(v) {
    private val cover = v.findViewById<ImageView>(R.id.cover)
    private val title = v.findViewById<TextView>(R.id.title)
    private val count = v.findViewById<TextView>(R.id.count)
    val cornerRadiusPx = (2 * itemView.resources.displayMetrics.density).toInt()
    fun bind(item: com.example.playlistmaker.domain.models.Playlist) {
        title.text = item.name
        count.text = "${item.playlistLength} треков"
        val uri = item.coverUri
        if (uri.isNullOrBlank()) cover.setImageResource(R.drawable.album_placeholder)
        else Glide.with(cover).
        load(android.net.Uri.parse(uri))
            .placeholder(R.drawable.album_placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusPx))
            .into(cover)
        itemView.setOnClickListener { onClick(item) }
    }
}