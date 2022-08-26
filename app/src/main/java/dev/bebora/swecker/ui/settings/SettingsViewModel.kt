package dev.bebora.swecker.ui.settings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.*
import dev.bebora.swecker.data.settings.SettingsRepositoryInterface
import dev.bebora.swecker.ui.utils.UiText
import dev.bebora.swecker.ui.utils.onError
import dev.bebora.swecker.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepositoryInterface,
    private val authService: AuthService,
    private val imageStorageService: ImageStorageService,
    private val accountsService: AccountsService
) : ViewModel() {
    val settings = repository.getSettings()

    var uiState by mutableStateOf(SettingsUiState())
        private set

    private val _accountUiEvent = Channel<UiEvent>()
    val accountUiEvent = _accountUiEvent.receiveAsFlow()

    private val _soundsUiEvent = Channel<UiEvent>()
    val soundsUiEvent = _soundsUiEvent.receiveAsFlow()

    private var userInfoChanges = authService.getUserInfoChanges()

    init {
        viewModelScope.launch {
            userInfoChanges.collect {
                // SuggestLogin checks for an empty userId and can be hidden
                uiState = uiState.copy(
                    me = User(id = authService.getUserId()),
                )
                accountsService.getUser(
                    userId = authService.getUserId(),
                    onSuccess = {
                        uiState = uiState.copy(
                            me = it
                        )
                    },
                    onError = ::onError
                )
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OpenEditName -> {
                uiState = uiState.copy(
                    showEditNamePopup = true,
                    currentName = uiState.me.name
                )
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

            SettingsEvent.OpenEditUsername -> {
                uiState = uiState.copy(
                    showEditUsernamePopup = true,
                    currentUsername = uiState.me.username
                )
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

            SettingsEvent.OpenEditDarkModeType -> {
                uiState = uiState.copy(
                    showEditDarkModeTypePopup = true
                )
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

            SettingsEvent.OpenEditRingtone -> {
                uiState = uiState.copy(
                    showEditRingtonePopup = true
                )
                viewModelScope.launch {
                    uiState = uiState.copy(
                        currentRingtone = settings.first().ringtone
                    )
                }
            }
            SettingsEvent.DismissEditRingtone -> {
                uiState = uiState.copy(
                    showEditRingtonePopup = false
                )
            }
            is SettingsEvent.SetTempRingtone -> {
                uiState = uiState.copy(
                    currentRingtone = event.ringtone
                )
            }
            is SettingsEvent.SetRingtone -> {
                viewModelScope.launch {
                    repository.setRingtone(event.ringtone)
                }
                uiState = uiState.copy(
                    showEditRingtonePopup = false
                )
            }

            SettingsEvent.OpenEditRingtoneDuration -> {
                uiState = uiState.copy(
                    showEditRingtoneDurationPopup = true
                )
            }
            SettingsEvent.DismissEditRingtoneDuration -> {
                uiState = uiState.copy(
                    showEditRingtoneDurationPopup = false
                )
            }
            is SettingsEvent.SetRingtoneDuration -> {
                viewModelScope.launch {
                    repository.setRingtoneDuration(event.ringtoneDuration)
                }
                uiState = uiState.copy(
                    showEditRingtoneDurationPopup = false
                )
            }

            SettingsEvent.OpenEditRingtoneVolume -> {
                uiState = uiState.copy(
                    showEditRingtoneVolumePopup = true
                )
                viewModelScope.launch {
                    uiState = uiState.copy(
                        currentRingtoneVolume = settings.first().ringtoneVolume
                    )
                }
            }
            SettingsEvent.DismissEditRingtoneVolume -> {
                uiState = uiState.copy(
                    showEditRingtoneVolumePopup = false
                )
            }
            is SettingsEvent.SetTempRingtoneVolume -> {
                uiState = uiState.copy(
                    currentRingtoneVolume = event.ringtoneVolume
                )
            }
            is SettingsEvent.SetRingtoneVolume -> {
                viewModelScope.launch {
                    repository.setRingtoneVolume(event.ringtoneVolume)
                }
                uiState = uiState.copy(
                    showEditRingtoneVolumePopup = false
                )
            }

            is SettingsEvent.SetPalette -> {
                viewModelScope.launch {
                    repository.setPalette(event.palette)
                }
            }

            is SettingsEvent.SetVibration -> {
                viewModelScope.launch {
                    repository.setVibration(event.enabled)
                }
                if (event.enabled) {
                    viewModelScope.launch {
                        _soundsUiEvent.send(UiEvent.VibrationFeedback)
                    }
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
                    openSoundsSettings = false,
                    openThemeSettings = false,
                )
            }
            SettingsEvent.OpenSoundsSettings -> {
                uiState = uiState.copy(
                    openAccountSettings = false,
                    openSoundsSettings = true,
                    openThemeSettings = false
                )
            }
            SettingsEvent.OpenThemeSettings -> {
                uiState = uiState.copy(
                    openAccountSettings = false,
                    openSoundsSettings = false,
                    openThemeSettings = true
                )
            }
            SettingsEvent.ToggleExampleAlarmActive -> {
                uiState = uiState.copy(
                    exampleAlarmActive = !uiState.exampleAlarmActive
                )
            }

            is SettingsEvent.SaveUser -> {
                uiState = uiState.copy(
                    showEditNamePopup = false,
                    showEditUsernamePopup = false,
                    accountLoading = true
                )
                accountsService.saveUser(event.user, oldUser = uiState.me) { error ->
                    uiState = uiState.copy(
                        accountLoading = false
                    )
                    if (error == null) {
                        uiState = uiState.copy(
                            me = uiState.me.copy(
                                name = event.user.name,
                                username = event.user.username.lowercase(),
                                propicUrl = event.user.propicUrl
                            )
                        )
                    } else {
                        onError(error)
                        val stringRes = when (error) {
                            is UsernameAlreadyTakenException -> R.string.unavailable_username
                            is BlankUserOrUsernameException -> R.string.blank_user_or_username
                            else -> R.string.save_user_error
                        }
                        viewModelScope.launch {
                            _accountUiEvent.send(
                                UiEvent.ShowSnackbar(
                                    uiText = UiText.StringResource(resId = stringRes)
                                )
                            )
                        }
                    }
                }
            }
            is SettingsEvent.SetProfilePicture -> {
                uiState = uiState.copy(
                    accountLoading = true
                )
                imageStorageService.setProfilePicture(
                    uiState.me.id,
                    event.imageUri,
                    onSuccess = {
                        onEvent(
                            SettingsEvent.SaveUser(
                                user = uiState.me.copy(
                                    propicUrl = it
                                )
                            )
                        )
                    },
                    onFailure = {
                        Log.d("SWECKER-SET-PROPIC", it)
                        uiState = uiState.copy(
                            accountLoading = false
                        )
                    }
                )
            }
            SettingsEvent.LogOut -> {
                authService.logOut()
            }
        }
    }
}
