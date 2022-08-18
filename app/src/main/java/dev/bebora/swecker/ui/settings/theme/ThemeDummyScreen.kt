package dev.bebora.swecker.ui.settings.theme

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import dev.bebora.swecker.ui.settings.SettingsUiState
import dev.bebora.swecker.ui.theme.*
import dev.bebora.swecker.ui.utils.darkModeTypeToString
import dev.bebora.swecker.ui.utils.paletteToColorSchemes
import dev.bebora.swecker.ui.utils.useDarkPalette
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDummyScreen(
    settings: Settings,
    ui: SettingsUiState,
    modifier: Modifier = Modifier,
    onEvent: (SettingsEvent) -> Unit
) {
    val darkModeEnabled = useDarkPalette(type = settings.darkModeType)
    val palettes = mutableListOf(
        Palette.VIOLET,
        Palette.GREEN,
        Palette.YELLOW
    )
    // Dynamic theme may crash the app on older versions
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        palettes.add(Palette.SYSTEM)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(R.string.theme_section_title)) },
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
            horizontalAlignment = Alignment.Start
        ) {
            AlarmCard(
                alarm = Alarm(
                    id = "fakeid",
                    enabled = ui.exampleAlarmActive,
                    name = "Example alarm",
                    alarmType = AlarmType.PERSONAL,
                    localDate = LocalDate.now(),
                    localTime = LocalTime.now()
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
                onEvent = { onEvent(SettingsEvent.ToggleExampleAlarmActive) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Palette",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                //TODO remove hardcoded values for width and height
                palettes.forEach { palette ->
                    val schemesWrapper = paletteToColorSchemes(palette = palette)
                    PaletteBox(
                        colorScheme = if (darkModeEnabled) schemesWrapper.darkColorScheme else schemesWrapper.lightColorScheme,
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                            .clickable { },
                        selected = settings.palette == palette,
                        showMagicIcon = palette == Palette.SYSTEM
                    ) { onEvent(SettingsEvent.SetPalette(palette = palette)) }
                }
            }
            SettingsItem(
                title = stringResource(R.string.dark_mode_dialog_title),
                description = darkModeTypeToString(type = settings.darkModeType),
                icon = Icons.Outlined.DarkMode
            ) {
                onEvent(SettingsEvent.OpenEditDarkModeType)
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
            ui = SettingsUiState(),
            onEvent = {}
        )
    }
}
