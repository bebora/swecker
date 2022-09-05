package dev.bebora.swecker.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.data.service.impl.ImageStorageServiceImpl
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.data.settings.FakeSettingsRepository
import dev.bebora.swecker.data.settings.Settings
import dev.bebora.swecker.ui.settings.account.AccountDummyScreen
import dev.bebora.swecker.ui.settings.main.SettingsDummyScreen
import dev.bebora.swecker.ui.settings.sounds.SoundsDummyScreen
import dev.bebora.swecker.ui.settings.theme.ThemeDummyScreen
import dev.bebora.swecker.ui.theme.SweckerTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onGoBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val settingsState by viewModel.settings.collectAsState(initial = Settings())
    //TODO find a way to prevent wrong UI settings from appearing (look at collectAsState with wrong initial value)

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (maxWidth < 840.dp) {
            if (viewModel.uiState.openAccountSettings) {
                BackHandler {
                    viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
                }
                AccountDummyScreen(
                    ui = viewModel.uiState,
                    onEvent = viewModel::onEvent,
                    uiEvent = viewModel.accountUiEvent
                ) { onNavigate(it) }
            } else if (viewModel.uiState.openSoundsSettings) {
                BackHandler {
                    viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
                }
                SoundsDummyScreen(
                    settings = settingsState,
                    ui = viewModel.uiState,
                    uiEvent = viewModel.soundsUiEvent,
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
                    onEvent = viewModel::onEvent,
                    onGoBack = onGoBack,
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsDummyScreen(
                    modifier = Modifier.weight(1f),
                    onEvent = viewModel::onEvent,
                    onGoBack = onGoBack,
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (viewModel.uiState.openAccountSettings) {
                        BackHandler {
                            viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
                        }
                        AccountDummyScreen(
                            ui = viewModel.uiState,
                            onEvent = viewModel::onEvent,
                            uiEvent = viewModel.accountUiEvent
                        ) { onNavigate(it) }
                    } else if (viewModel.uiState.openSoundsSettings) {
                        BackHandler {
                            viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
                        }
                        SoundsDummyScreen(
                            settings = settingsState,
                            ui = viewModel.uiState,
                            uiEvent = viewModel.soundsUiEvent,
                            onEvent = viewModel::onEvent
                        )
                    } else if (viewModel.uiState.openThemeSettings) {
                        BackHandler {
                            viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
                        }
                        ThemeDummyScreen(
                            settings = settingsState,
                            ui = viewModel.uiState,
                            onEvent = viewModel::onEvent,
                        )
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
            modifier = Modifier,
            viewModel = SettingsViewModel(
                repository = FakeSettingsRepository(),
                authService = FakeAuthService(),
                accountsService = FakeAccountsService(
                    users = FakeAccountsService.defaultUsers.toMutableMap(),
                    friendshipRequests = FakeAccountsService.defaultFriendshipRequests.toMutableMap()
                ),
                imageStorageService = ImageStorageServiceImpl()
            )
        )
    }
}
