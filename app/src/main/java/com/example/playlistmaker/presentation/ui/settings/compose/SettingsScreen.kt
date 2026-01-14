package com.example.playlistmaker.presentation.ui.settings.compose

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.YsDisplay
import com.example.playlistmaker.presentation.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val isDarkTheme by viewModel.themeState.observeAsState(false)

    LaunchedEffect(isDarkTheme) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.settings_background_color))
            .padding(4.dp)
    ) {

        Text(
            text = stringResource(R.string.settings),
            fontFamily = YsDisplay,
            fontWeight = FontWeight.W500,
            fontSize = 22.sp,
            color = colorResource(R.color.text_black),
            modifier = Modifier.padding(start = 12.dp, top = 10.dp, bottom = 14.dp)
        )

        // === Dark theme row ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.buttons_margin))
                .padding(start = dimensionResource(R.dimen.screen_padding))
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.dark_theme),
                fontFamily = YsDisplay,
                fontWeight = FontWeight.W400,
                fontSize = 16.sp,
                color = colorResource(R.color.text_black),
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = isDarkTheme,
                onCheckedChange = { viewModel.setDarkTheme(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colorResource(R.color.switch_thumb_active_color),
                    uncheckedThumbColor = colorResource(R.color.switch_thumb_inactive_color),

                    checkedTrackColor = colorResource(R.color.switch_track_active_color),
                    uncheckedTrackColor = colorResource(R.color.switch_track_inactive_color),

                    checkedBorderColor = colorResource(R.color.switch_track_active_color),
                    uncheckedBorderColor = colorResource(R.color.switch_track_inactive_color),
                ),
                modifier = Modifier.padding(end = 6.dp)
            )
        }

        SettingsOptionRow(
            text = stringResource(R.string.share_app),
            iconRes = R.drawable.share,
            onClick = {
                val shareText = context.getString(R.string.share_text)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(
                        Intent.createChooser(intent, context.getString(R.string.choose_app))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.no_such_app),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        SettingsOptionRow(
            text = stringResource(R.string.write_to_support),
            iconRes = R.drawable.support,
            onClick = {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(
                        Intent.EXTRA_EMAIL,
                        arrayOf(context.getString(R.string.mail_address_example))
                    )
                    putExtra(Intent.EXTRA_TEXT, context.getString(R.string.mail_message))
                    putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.mail_subject))
                }
                context.startActivity(emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        )

        SettingsOptionRow(
            text = stringResource(R.string.user_agreement),
            iconRes = R.drawable.arrow_forward,
            onClick = {
                val url = context.getString(R.string.agreement_url)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        )
    }
}

@Composable
private fun SettingsOptionRow(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    val iconSize = dimensionResource(R.dimen.icon_size)
    val iconMarginEnd = dimensionResource(R.dimen.settings_options_icon_margin)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(vertical = dimensionResource(R.dimen.settings_options_padding_vertical))
            .padding(start = dimensionResource(R.dimen.screen_padding)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontFamily = YsDisplay,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            color = colorResource(R.color.text_black),
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorResource(R.color.icon_gray)),
            modifier = Modifier.size(iconSize)
        )

        Spacer(Modifier.width(iconMarginEnd))
    }
}