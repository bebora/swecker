package dev.bebora.swecker.ui.settings.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.bebora.swecker.R
import dev.bebora.swecker.data.settings.Settings
import dev.bebora.swecker.ui.settings.*
import dev.bebora.swecker.ui.theme.SweckerTheme

/**
 * Initial page of the Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDummyScreen(
    modifier: Modifier = Modifier,
    onEvent: (SettingsEvent) -> Unit,
    onGoBack: () -> Unit = {},
) {
    val sections = listOf(
        SettingsSection(
            stringResource(R.string.account_section_title),
            stringResource(R.string.account_section_description),
            Icons.Outlined.AccountCircle
        ) { onEvent(SettingsEvent.OpenAccountSettings) },
        SettingsSection(
            stringResource(R.string.theme_section_title),
            stringResource(R.string.theme_section_description),
            Icons.Outlined.Palette
        ) { onEvent(SettingsEvent.OpenThemeSettings) },
        SettingsSection(
            stringResource(R.string.sounds_section_title),
            stringResource(R.string.sounds_section_description),
            Icons.Outlined.NotificationsActive
        ) { onEvent(SettingsEvent.OpenSoundsSettings) },
        /*SettingsSection(
            stringResource(R.string.about_section_title),
            stringResource(R.string.about_section_description),
            Icons.Outlined.Info
        ) {
        }*/
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { onGoBack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(state = rememberScrollState()),
        ) {
            sections.forEach { section ->
                Divider()
                SettingsItem(
                    title = section.title,
                    description = section.description ?: "Default description",
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
        SettingsDummyScreen(
            onEvent = {}
        ) {}
    }
}
