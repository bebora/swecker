package dev.bebora.swecker.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.data.settings.DataStoreManager
import dev.bebora.swecker.data.settings.Settings
import dev.bebora.swecker.ui.settings.account.AccountDummyScreen
import dev.bebora.swecker.ui.settings.main.SettingsDummyScreen
import dev.bebora.swecker.ui.settings.sounds.SoundsDummyScreen
import dev.bebora.swecker.ui.settings.theme.ThemeDummyScreen
import dev.bebora.swecker.ui.theme.SettingsAwareTheme
import dev.bebora.swecker.ui.theme.SweckerTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsState by viewModel.settings.collectAsState(initial = Settings())
    //TODO find a way to prevent wrong UI settings from appearing (look at collectAsState with wrong initial value)
    SettingsAwareTheme(
        darkModeType = settingsState.darkModeType,
        palette = settingsState.palette
    ) {
        if (viewModel.uiState.openAccountSettings) {
            AccountDummyScreen(
                settings = settingsState,
                ui = viewModel.uiState,
                onEvent = viewModel::onEvent
            )
        } else if (viewModel.uiState.openSoundsSettings) {
            SoundsDummyScreen(
                settings = settingsState,
                ui = viewModel.uiState,
                onEvent = viewModel::onEvent
            )
        } else if (viewModel.uiState.openThemeSettings) {
            ThemeDummyScreen(
                settings = settingsState,
                ui = viewModel.uiState,
                onEvent = viewModel::onEvent
            )
        } else {
            SettingsDummyScreen(
                settings = settingsState,
                ui = viewModel.uiState,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SweckerTheme {
        SettingsScreen(
            viewModel = SettingsViewModel(
                repository = DataStoreManager(
                    LocalContext.current
                )
            )
        )
    }
}
