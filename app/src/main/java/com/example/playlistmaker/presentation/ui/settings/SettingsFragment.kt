package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.presentation.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.themeState.observe(viewLifecycleOwner) { isDarkTheme ->
            binding.themeSwitch.setOnCheckedChangeListener(null)
            binding.themeSwitch.isChecked = isDarkTheme
            binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setDarkTheme(isChecked)
            }
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkTheme(isChecked)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun shareApp() {
        val shareText = getString(R.string.share_text)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(Intent.createChooser(intent, getString(R.string.choose_app)))
        } else {
            Toast.makeText(requireContext(), getString(R.string.no_such_app), Toast.LENGTH_SHORT).show()
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