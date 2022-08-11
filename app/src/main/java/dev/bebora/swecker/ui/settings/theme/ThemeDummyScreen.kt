package dev.bebora.swecker.ui.settings.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.settings.SettingsRepositoryPreview
import dev.bebora.swecker.ui.alarm_browser.AlarmCard
import dev.bebora.swecker.ui.settings.SettingsEvent
import dev.bebora.swecker.ui.settings.SettingsItem
import dev.bebora.swecker.ui.settings.SettingsViewModel
import dev.bebora.swecker.ui.theme.DarkColors
import dev.bebora.swecker.ui.theme.LightColors
import dev.bebora.swecker.ui.theme.SweckerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDummyScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    //TODO add real themes
    val themes = listOf(
        dynamicDarkColorScheme(LocalContext.current),
        dynamicLightColorScheme(LocalContext.current),
        DarkColors,
        LightColors
    )
    Scaffold(topBar = {
        SmallTopAppBar(
            title = { Text(text = stringResource(R.string.account_title)) },
            navigationIcon = {
                IconButton(onClick = { viewModel.onEvent(SettingsEvent.CloseSettingsSubsection) }) {
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
                    enabled = true,
                    name = "Example alarm",
                    alarmType = AlarmType.PERSONAL,
                    date = "9th August",
                    time = "12:14"
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Palette", style = MaterialTheme.typography.headlineSmall)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                //TODO remove hardcoded values
                themes.forEach { palette ->
                    PaletteSelector(
                        palette = palette,
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            SettingsItem(
                title = "Dark mode",
                description = "Use system settings",
                icon = Icons.Outlined.DarkMode
            ) {

            }

        }
    }
}

@Preview(locale = "en")
@Composable
fun ThemeDummyScreenPreview() {
    SweckerTheme {
        ThemeDummyScreen(
            viewModel = SettingsViewModel(SettingsRepositoryPreview())
        )
    }
}
