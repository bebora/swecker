package dev.bebora.swecker.ui.settings

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.data.service.impl.AccountServiceImpl
import dev.bebora.swecker.data.service.impl.StorageServiceImpl
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
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit = {}
) {
    LaunchedEffect(key1 = true) {
        viewModel.initialize()
    }

    val settingsState by viewModel.settings.collectAsState(initial = Settings())
    //TODO find a way to prevent wrong UI settings from appearing (look at collectAsState with wrong initial value)
    SettingsAwareTheme(
        darkModeType = settingsState.darkModeType,
        palette = settingsState.palette
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth < 840.dp) {
                if (viewModel.uiState.openAccountSettings) {
                    BackHandler {
                        viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
                    }
                    AccountDummyScreen(
                        ui = viewModel.uiState,
                        onEvent = viewModel::onEvent,
                        onNavigate = { onNavigate(it) }
                    )
                } else if (viewModel.uiState.openSoundsSettings) {
                    BackHandler {
                        viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
                    }
                    SoundsDummyScreen(
                        settings = settingsState,
                        ui = viewModel.uiState,
                        onEvent = viewModel::onEvent
                    )
                } else if (viewModel.uiState.openThemeSettings) {
                    BackHandler {
                        viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
                    }
                    ThemeDummyScreen(
                        settings = settingsState,
                        ui = viewModel.uiState,
                        onEvent = viewModel::onEvent
                    )
                } else {
                    SettingsDummyScreen(
                        settings = settingsState,
                        ui = viewModel.uiState,
                        onEvent = viewModel::onEvent,
                        onNavigate = onNavigate
                    )
                }
            } else {
                Row {
                    SettingsDummyScreen(
                        settings = settingsState,
                        ui = viewModel.uiState,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier.weight(1f),
                        onNavigate = onNavigate
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (viewModel.uiState.openAccountSettings) {
                            BackHandler {
                                viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
                            }
                            AccountDummyScreen(
                                ui = viewModel.uiState,
                                onEvent = viewModel::onEvent,
                                onNavigate = { onNavigate(it) }
                            )
                        } else if (viewModel.uiState.openSoundsSettings) {
                            BackHandler {
                                viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
                            }
                            SoundsDummyScreen(
                                settings = settingsState,
                                ui = viewModel.uiState,
                                onEvent = viewModel::onEvent
                            )
                        } else if (viewModel.uiState.openThemeSettings) {
                            BackHandler {
                                viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
                            }
                            ThemeDummyScreen(
                                settings = settingsState,
                                ui = viewModel.uiState,
                                onEvent = viewModel::onEvent
                            )
                        }
                    }
                }
            }
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
                ),
                application = Application(),
                accountService = AccountServiceImpl(),
                storageService = StorageServiceImpl()
            )
        )
    }
}
