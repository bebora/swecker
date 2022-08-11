package dev.bebora.swecker.ui.settings.sounds

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
import dev.bebora.swecker.R
import dev.bebora.swecker.data.settings.Settings
import dev.bebora.swecker.ui.settings.*
import dev.bebora.swecker.ui.theme.SweckerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundsDummyScreen(
    settings: Settings,
    ui: SettingsUI,
    onEvent: (SettingsEvent) -> Unit
) {
    val sections = listOf(
        SettingsSection(
            stringResource(R.string.sounds_ringtone),
            settings.ringtone.toString(),
            Icons.Outlined.MusicNote
        ),
        SettingsSection(
            stringResource(R.string.sounds_ringtone_duration),
            settings.ringtoneDuration.toString(),
            Icons.Outlined.Timer
        ),
        SettingsSection(
            stringResource(R.string.sounds_volume),
            "${settings.ringtoneVolume}%",
            Icons.Outlined.VolumeUp
        )
    )
    Scaffold(topBar = {
        SmallTopAppBar(
            title = { Text(text = stringResource(id = R.string.sounds_section_title)) },
            navigationIcon = {
                IconButton(onClick = { onEvent(SettingsEvent.CloseSettingsSubsection) }) {
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
                    description = section.description ?: "Default description",
                    icon = section.icon,
                    onClick = section.onClick
                )

            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )
            SettingsSwitch(
                title = stringResource(R.string.sounds_vibration),
                icon = Icons.Outlined.Vibration,
                checked = settings.vibration
            ) {
                onEvent(SettingsEvent.ToggleVibration)
            }
        }
    }
}

@Preview(locale = "en")
@Composable
fun SoundsDummyScreenPreview() {
    SweckerTheme {
        SoundsDummyScreen(
            settings = Settings(),
            ui = SettingsUI(),
            onEvent = {}
        )
    }
}
