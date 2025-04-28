package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var themeSwitch: SwitchMaterial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        sharedPrefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkTheme = sharedPrefs.getBoolean("dark_theme", false)
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        val backButton = findViewById<ImageView>(R.id.back_to_main_menu)

        backButton.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }
        themeSwitch = findViewById(R.id.theme_switch)
        themeSwitch.isChecked = isDarkTheme
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            sharedPrefs.edit().putBoolean("dark_theme", isChecked).apply()
        }
        val shareButton = findViewById<LinearLayout>(R.id.share_button)
        shareButton.setOnClickListener {
            val shareText = getString(R.string.share_text)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            if (shareIntent.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_app)))
            } else {
                Toast.makeText(this, getString(R.string.no_such_app), Toast.LENGTH_SHORT).show()
            }
        }

        val supportButton = findViewById<LinearLayout>(R.id.support_button)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_address_example)))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_message))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
            startActivity(supportIntent)
        }
        val aggreementButton = findViewById<LinearLayout>(R.id.agreement_button)
        aggreementButton.setOnClickListener{
            val url = getString(R.string.agreement_url)
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(agreementIntent)
        }
    }
}