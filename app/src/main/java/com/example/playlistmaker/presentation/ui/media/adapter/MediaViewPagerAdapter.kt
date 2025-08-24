package com.example.playlistmaker.presentation.ui.media.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.presentation.ui.media.FavoritesFragment
import com.example.playlistmaker.presentation.ui.media.PlaylistsFragment

class MediaViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            1 -> PlaylistsFragment.newInstance()
            else -> FavoritesFragment.newInstance()
        }
    }
}