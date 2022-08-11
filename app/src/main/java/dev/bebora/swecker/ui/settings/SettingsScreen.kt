package dev.bebora.swecker.ui.settings

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.ui.settings.account.AccountDummyScreen
import dev.bebora.swecker.ui.settings.main.SettingsDummyScreen
import dev.bebora.swecker.ui.settings.sounds.SoundsDummyScreen
import dev.bebora.swecker.ui.settings.theme.ThemeDummyScreen

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    //TODO pass state to DummyScreens and not the whole ViewModel
    //TODO find a way to prevent wrong UI settings from appearing (look at collectAsState with wrong initial value)
    if (viewModel.openAccountSettings) {
        AccountDummyScreen()
    } else if (viewModel.openSoundsSettings) {
        SoundsDummyScreen()
    } else if (viewModel.openThemeSettings) {
        ThemeDummyScreen()
    } else {
        SettingsDummyScreen()
    }
}
