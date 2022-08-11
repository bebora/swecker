package dev.bebora.swecker.ui.settings.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.settings.SettingsEvent
import dev.bebora.swecker.ui.settings.SettingsItem
import dev.bebora.swecker.ui.settings.SettingsSection
import dev.bebora.swecker.ui.settings.SettingsViewModel
import dev.bebora.swecker.ui.theme.SweckerTheme

/**
 * Initial page of the Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDummyScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val sections = listOf(
        SettingsSection(
            stringResource(R.string.account_section_title),
            stringResource(R.string.account_section_description),
            Icons.Outlined.AccountCircle
        ) { viewModel.onEvent(SettingsEvent.OpenAccountSettings) },
        SettingsSection(
            stringResource(R.string.theme_section_title),
            stringResource(R.string.theme_section_description),
            Icons.Outlined.Palette
        ) { viewModel.onEvent(SettingsEvent.OpenThemeSettings) },
        SettingsSection(
            stringResource(R.string.sounds_section_title),
            stringResource(R.string.sounds_section_description),
            Icons.Outlined.NotificationsActive
        ) { viewModel.onEvent(SettingsEvent.OpenSoundsSettings) },
        SettingsSection(
            stringResource(R.string.about_section_title),
            stringResource(R.string.about_section_description),
            Icons.Outlined.Info
        )
    )
    Scaffold(topBar = {
        SmallTopAppBar(
            title = { Text(text = stringResource(R.string.settings_title)) },
            navigationIcon = {
                IconButton(onClick = { /*TODO go back*/ }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }
        )
    }) {
        Column(Modifier.padding(it)) {
            sections.forEach { section ->
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
                SettingsItem(
                    title = section.title,
                    description = section.description?: "Default description",
                    icon = section.icon,
                    onClick = section.onClick
                )
            }
        }
    }
}

@Preview(locale = "en")
@Composable
fun SettingsDummyScreenPreview() {
    SweckerTheme {
        SettingsDummyScreen()
    }
}
