package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.settings.SettingsInteractorImpl
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPrefs = getSharedPreferences("settings", MODE_PRIVATE)
        settingsInteractor = SettingsInteractorImpl(sharedPrefs)

        val isDarkTheme = settingsInteractor.isDarkThemeEnabled()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        findViewById<ImageView>(R.id.back_to_main_menu).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        themeSwitch = findViewById(R.id.theme_switch)
        themeSwitch.isChecked = isDarkTheme
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            settingsInteractor.setDarkThemeEnabled(isChecked)
        }

        findViewById<LinearLayout>(R.id.share_button).setOnClickListener {
            shareApp()
        }

        findViewById<LinearLayout>(R.id.support_button).setOnClickListener {
            sendSupportEmail()
        }

        findViewById<LinearLayout>(R.id.agreement_button).setOnClickListener {
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
