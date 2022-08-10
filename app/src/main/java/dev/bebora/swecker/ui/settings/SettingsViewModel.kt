package dev.bebora.swecker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.settings.SettingsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {
    val settings = repository.getSettings()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SetName -> {
                viewModelScope.launch {
                    repository.setName(event.name)
                }
            }
            is SettingsEvent.SetUsername -> {
                viewModelScope.launch {
                    repository.setUsername(event.username)
                }
            }
            is SettingsEvent.SetDarkModeType -> {
                viewModelScope.launch {
                    repository.setDarkModeType(event.darkModeType)
                }
            }
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
        }
    }
}
