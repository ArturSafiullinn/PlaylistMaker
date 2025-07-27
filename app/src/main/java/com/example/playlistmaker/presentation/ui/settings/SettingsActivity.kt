package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.presentation.ui.main.MainActivity
import com.example.playlistmaker.presentation.viewmodel.SettingsViewModel
import com.example.playlistmaker.presentation.viewmodel.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels { SettingsViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.themeState.observe(this) { isDarkTheme ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            binding.themeSwitch.isChecked = isDarkTheme
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkTheme(isChecked)
        }

        binding.backToMainMenu.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.shareButton.setOnClickListener {
            shareApp()
        }

        binding.supportButton.setOnClickListener {
            sendSupportEmail()
        }

        binding.agreementButton.setOnClickListener {
            openAgreement()
        }
    }

    private fun shareApp() {
        val shareText = getString(R.string.share_text)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, getString(R.string.choose_app)))
        } else {
            Toast.makeText(this, getString(R.string.no_such_app), Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSupportEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_address_example)))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_message))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
        }
        startActivity(emailIntent)
    }

    private fun openAgreement() {
        val url = getString(R.string.agreement_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}