package dev.bebora.swecker.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.settings.SettingsRepositoryInterface
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepositoryInterface
) : ViewModel() {
    val settings = repository.getSettings()

    var uiState by mutableStateOf(SettingsUI())
        private set

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OpenEditName -> {
                uiState = uiState.copy(
                    showEditNamePopup = true
                )
                viewModelScope.launch {
                    uiState = uiState.copy(
                        currentName = settings.first().name
                    )
                }
            }
            SettingsEvent.DismissEditName -> {
                uiState = uiState.copy(
                    showEditNamePopup = false
                )
            }
            is SettingsEvent.SetTempName -> {
                uiState = uiState.copy(
                    currentName = event.name
                )
            }
            is SettingsEvent.SetName -> {
                viewModelScope.launch {
                    repository.setName(event.name)
                }
                uiState = uiState.copy(
                    showEditNamePopup = false
                )
            }

            SettingsEvent.OpenEditUsername -> {
                uiState = uiState.copy(
                    showEditUsernamePopup = true
                )
                viewModelScope.launch {
                    uiState = uiState.copy(
                        currentUsername = settings.first().username
                    )
                }
            }
            SettingsEvent.DismissEditUsername -> {
                uiState = uiState.copy(
                    showEditUsernamePopup = false
                )
            }
            is SettingsEvent.SetTempUsername -> {
                uiState = uiState.copy(
                    currentUsername = event.username
                )
            }
            is SettingsEvent.SetUsername -> {
                viewModelScope.launch {
                    repository.setUsername(event.username)
                }
                uiState = uiState.copy(
                    showEditUsernamePopup = false
                )
            }

            SettingsEvent.OpenEditDarkModeType -> {
                uiState = uiState.copy(
                    showEditDarkModeTypePopup = true
                )
                viewModelScope.launch {
                    uiState = uiState.copy(
                        currentDarkModeType = settings.first().darkModeType
                    )
                }
            }
            SettingsEvent.DismissEditDarkModeType -> {
                uiState = uiState.copy(
                    showEditDarkModeTypePopup = false
                )
            }
            is SettingsEvent.SetDarkModeType -> {
                viewModelScope.launch {
                    repository.setDarkModeType(event.darkModeType)
                }
                uiState = uiState.copy(
                    showEditDarkModeTypePopup = false
                )
            }

            //TODO setters should update the ui state
            is SettingsEvent.SetPalette -> {
                viewModelScope.launch {
                    repository.setPalette(event.palette)
                }
            }
            is SettingsEvent.SetRingtone -> {
                viewModelScope.launch {
                    repository.setRingtone(event.ringtone)
                }
            }
            is SettingsEvent.SetRingtoneDuration -> {
                viewModelScope.launch {
                    repository.setRingtoneDuration(event.ringtoneDuration)
                }
            }
            is SettingsEvent.SetRingtoneVolume -> {
                viewModelScope.launch {
                    repository.setRingtoneVolume(event.ringtoneVolume)
                }
            }
            SettingsEvent.ToggleVibration -> {
                viewModelScope.launch {
                    repository.toggleVibration()
                }
            }
            SettingsEvent.CloseSettingsSubsection -> {
                uiState = uiState.copy(
                    openAccountSettings = false,
                    openSoundsSettings = false,
                    openThemeSettings = false
                )
            }
            SettingsEvent.OpenAccountSettings -> {
                uiState = uiState.copy(
                    openAccountSettings = true,
                )
            }
            SettingsEvent.OpenSoundsSettings -> {
                uiState = uiState.copy(
                    openSoundsSettings = true,
                )
            }
            SettingsEvent.OpenThemeSettings -> {
                uiState = uiState.copy(
                    openThemeSettings = true
                )
            }
            SettingsEvent.ToggleExampleAlarmActive -> {
                uiState = uiState.copy(
                    exampleAlarmActive = !uiState.exampleAlarmActive
                )
            }
        }
    }
}
