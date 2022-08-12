package dev.bebora.swecker.ui.settings

import dev.bebora.swecker.data.settings.DarkModeType
import dev.bebora.swecker.data.settings.Palette
import dev.bebora.swecker.data.settings.Ringtone
import dev.bebora.swecker.data.settings.RingtoneDuration

/**
 * Events sent from the View that should update the Settings Model
 */
sealed class SettingsEvent {
    object OpenEditName: SettingsEvent()
    object DismissEditName: SettingsEvent()
    data class SetTempName(val name: String) : SettingsEvent()
    data class SetName(val name: String) : SettingsEvent()

    object OpenEditUsername: SettingsEvent()
    object DismissEditUsername: SettingsEvent()
    data class SetTempUsername(val username: String) : SettingsEvent()
    data class SetUsername(val username: String) : SettingsEvent()

    object OpenEditDarkModeType: SettingsEvent()
    object DismissEditDarkModeType: SettingsEvent()
    data class SetDarkModeType(val darkModeType: DarkModeType) : SettingsEvent()

    data class SetPalette(val palette: Palette) : SettingsEvent()
    data class SetRingtone(val ringtone: Ringtone) : SettingsEvent()
    data class SetRingtoneVolume(val ringtoneVolume: Int) : SettingsEvent()
    data class SetRingtoneDuration(val ringtoneDuration: RingtoneDuration) : SettingsEvent()
    object ToggleVibration : SettingsEvent()
    object OpenAccountSettings : SettingsEvent()
    object OpenSoundsSettings : SettingsEvent()
    object OpenThemeSettings : SettingsEvent()
    object CloseSettingsSubsection : SettingsEvent()
    object ToggleExampleAlarmActive: SettingsEvent()
}
