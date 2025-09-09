package com.example.playlistmaker.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityHostBinding
import com.example.playlistmaker.presentation.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.searchFragment,
                R.id.mediaFragment,
                R.id.settingsFragment -> binding.bottomNavigationView.show()
                else -> binding.bottomNavigationView.hide()
            }
        }
    }
}

private fun com.google.android.material.bottomnavigation.BottomNavigationView.show() {
    if (visibility != android.view.View.VISIBLE) visibility = android.view.View.VISIBLE
}
private fun com.google.android.material.bottomnavigation.BottomNavigationView.hide() {
    if (visibility != android.view.View.GONE) visibility = android.view.View.GONE
}
