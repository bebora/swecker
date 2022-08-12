package dev.bebora.swecker.ui.settings.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.R
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.settings.DarkModeType
import dev.bebora.swecker.data.settings.Palette
import dev.bebora.swecker.data.settings.Settings
import dev.bebora.swecker.ui.alarm_browser.AlarmCard
import dev.bebora.swecker.ui.settings.SettingsEvent
import dev.bebora.swecker.ui.settings.SettingsItem
import dev.bebora.swecker.ui.settings.SettingsUI
import dev.bebora.swecker.ui.theme.DarkColors
import dev.bebora.swecker.ui.theme.LightColors
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.ui.utils.darkModeTypeToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDummyScreen(
    settings: Settings,
    ui: SettingsUI,
    onEvent: (SettingsEvent) -> Unit
) {
    //TODO add real themes
    val palettes = listOf(
        PaletteData(
            colorScheme = dynamicDarkColorScheme(LocalContext.current),
            onClick = { onEvent(SettingsEvent.SetPalette(Palette.SYSTEM)) }),
        PaletteData(
            colorScheme = DarkColors,
            onClick = { onEvent(SettingsEvent.SetPalette(Palette.VARIATION1)) }),
        PaletteData(
            colorScheme = LightColors,
            onClick = { onEvent(SettingsEvent.SetPalette(Palette.VARIATION2)) }),
        PaletteData(
            colorScheme = DarkColors,
            onClick = { onEvent(SettingsEvent.SetPalette(Palette.VARIATION3)) })
    )

    Box {
        Scaffold(topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(R.string.account_title)) },
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
            Column(
                Modifier
                    .padding(it)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                AlarmCard(
                    alarm = Alarm(
                        id = "fakeid",
                        enabled = ui.exampleAlarmActive,
                        name = "Example alarm",
                        alarmType = AlarmType.PERSONAL,
                        date = "9th August",
                        time = "12:14"
                    ),
                    onEvent = { onEvent(SettingsEvent.ToggleExampleAlarmActive) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Palette", style = MaterialTheme.typography.headlineSmall)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    //TODO remove hardcoded values for width and height
                    palettes.forEachIndexed { idx, palette ->
                        PaletteBox(
                            colorScheme = palette.colorScheme,
                            modifier = Modifier
                                .width(80.dp)
                                .height(80.dp)
                                .clickable { },
                            selected = settings.palette.ordinal == idx //TODO this logic may break if the Enum order is not mantained
                        ) { palette.onClick() }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                SettingsItem(
                    title = stringResource(R.string.dark_mode_dialog_title),
                    description = darkModeTypeToString(type = settings.darkModeType),
                    icon = Icons.Outlined.DarkMode
                ) {
                    onEvent(SettingsEvent.OpenEditDarkModeType)
                }

            }
        }
    }

    if (ui.showEditDarkModeTypePopup) {
        //FIXME radio buttons and text have padding from the AlertDialog, is it possible to remove it?
        AlertDialog(
            onDismissRequest = { onEvent(SettingsEvent.DismissEditDarkModeType) },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { onEvent(SettingsEvent.DismissEditDarkModeType) }) {
                    Text(text = stringResource(R.string.dismiss_dialog))
                }
            },
            text = {
                LazyColumn {
                    items(DarkModeType.values()) { type ->
                        Row(
                            verticalAlignment = CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onEvent(
                                        SettingsEvent.SetDarkModeType(
                                            type
                                        )
                                    )
                                })
                        {
                            RadioButton(
                                selected = type == settings.darkModeType,
                                onClick = {})
                            Text(
                                text = darkModeTypeToString(type = type),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            title = { Text(stringResource(R.string.dark_mode_dialog_title)) }
        )
    }
}

@Preview(locale = "en")
@Composable
fun ThemeDummyScreenPreview() {
    SweckerTheme {
        ThemeDummyScreen(
            settings = Settings(),
            ui = SettingsUI(),
            onEvent = {}
        )
    }
}
