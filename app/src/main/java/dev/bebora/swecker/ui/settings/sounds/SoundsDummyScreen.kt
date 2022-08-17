package dev.bebora.swecker.ui.settings.sounds

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.R
import dev.bebora.swecker.data.settings.Ringtone
import dev.bebora.swecker.data.settings.RingtoneDuration
import dev.bebora.swecker.data.settings.Settings
import dev.bebora.swecker.ui.settings.*
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.ui.utils.ringtoneDurationToString
import dev.bebora.swecker.ui.utils.ringtoneToString
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundsDummyScreen(
    settings: Settings,
    ui: SettingsUiState,
    modifier: Modifier = Modifier,
    onEvent: (SettingsEvent) -> Unit
) {
    val sections = listOf(
        SettingsSection(
            stringResource(R.string.sounds_ringtone),
            ringtoneToString(tone = settings.ringtone),
            Icons.Outlined.MusicNote,
            onClick = { onEvent(SettingsEvent.OpenEditRingtone) }
        ),
        SettingsSection(
            stringResource(R.string.sounds_ringtone_duration),
            ringtoneDurationToString(duration = settings.ringtoneDuration),
            Icons.Outlined.Timer,
            onClick = { onEvent(SettingsEvent.OpenEditRingtoneDuration) }
        ),
        SettingsSection(
            stringResource(R.string.sounds_volume),
            "${settings.ringtoneVolume}%",
            Icons.Outlined.VolumeUp,
            onClick = { onEvent(SettingsEvent.OpenEditRingtoneVolume) }
        )
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.sounds_section_title)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(SettingsEvent.CloseSettingsSubsection) }) {
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
                onEvent(SettingsEvent.SetVibration(!settings.vibration))
            }
        }
    }
    if (ui.showEditRingtonePopup) {
        AlertDialog(
            onDismissRequest = { onEvent(SettingsEvent.DismissEditRingtone) },
            confirmButton = {
                TextButton(onClick = { onEvent(SettingsEvent.SetRingtone(ui.currentRingtone)) }) {
                    Text(text = stringResource(R.string.confirm_dialog))
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(SettingsEvent.DismissEditRingtone) }) {
                    Text(text = stringResource(R.string.dismiss_dialog))
                }
            },
            text = {
                LazyColumn {
                    items(Ringtone.values()) { tone ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onEvent(
                                        SettingsEvent.SetTempRingtone(
                                            tone
                                        )
                                    )
                                })
                        {
                            RadioButton(
                                selected = tone == ui.currentRingtone,
                                onClick = {})
                            Text(
                                text = ringtoneToString(tone = tone),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            title = { Text(stringResource(R.string.sounds_ringtone)) }
        )
    }
    if (ui.showEditRingtoneDurationPopup) {
        AlertDialog(
            onDismissRequest = { onEvent(SettingsEvent.DismissEditRingtoneDuration) },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { onEvent(SettingsEvent.DismissEditRingtoneDuration) }) {
                    Text(text = stringResource(R.string.dismiss_dialog))
                }
            },
            text = {
                LazyColumn {
                    items(RingtoneDuration.values()) { duration ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onEvent(
                                        SettingsEvent.SetRingtoneDuration(
                                            duration
                                        )
                                    )
                                })
                        {
                            RadioButton(
                                selected = duration == settings.ringtoneDuration,
                                onClick = {})
                            Text(
                                text = ringtoneDurationToString(duration = duration),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            title = { Text(stringResource(R.string.sounds_ringtone_duration)) }
        )
    }
    if (ui.showEditRingtoneVolumePopup) {
        AlertDialog(
            onDismissRequest = { onEvent(SettingsEvent.DismissEditRingtoneVolume) },
            confirmButton = {
                TextButton(onClick = { onEvent(SettingsEvent.SetRingtoneVolume(ui.currentRingtoneVolume)) }) {
                    Text(text = stringResource(R.string.confirm_dialog))
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(SettingsEvent.DismissEditRingtoneVolume) }) {
                    Text(text = stringResource(R.string.dismiss_dialog))
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${ui.currentRingtoneVolume}%",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = ui.currentRingtoneVolume / 100F,
                        onValueChange = {
                            onEvent(
                                SettingsEvent.SetTempRingtoneVolume(
                                    (100F * it).roundToInt()
                                )
                            )
                        },
                        steps = 9
                    )
                }
            },
            title = { Text(stringResource(R.string.sounds_ringtone_volume)) }
        )
    }
}

@Preview(locale = "en")
@Composable
fun SoundsDummyScreenPreview() {
    SweckerTheme {
        SoundsDummyScreen(
            settings = Settings(),
            ui = SettingsUiState(),
            onEvent = {}
        )
    }
}
