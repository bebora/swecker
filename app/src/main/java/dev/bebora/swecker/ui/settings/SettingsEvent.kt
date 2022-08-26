package dev.bebora.swecker.ui.settings

import android.net.Uri
import dev.bebora.swecker.data.User
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

    object OpenEditUsername: SettingsEvent()
    object DismissEditUsername: SettingsEvent()
    data class SetTempUsername(val username: String) : SettingsEvent()

    object OpenEditDarkModeType: SettingsEvent()
    object DismissEditDarkModeType: SettingsEvent()
    data class SetDarkModeType(val darkModeType: DarkModeType) : SettingsEvent()

    data class SetPalette(val palette: Palette) : SettingsEvent()

    object OpenEditRingtone: SettingsEvent()
    object DismissEditRingtone: SettingsEvent()
    data class SetTempRingtone(val ringtone: Ringtone) : SettingsEvent()
    data class SetRingtone(val ringtone: Ringtone) : SettingsEvent()

    object OpenEditRingtoneVolume: SettingsEvent()
    object DismissEditRingtoneVolume: SettingsEvent()
    data class SetTempRingtoneVolume(val ringtoneVolume: Int) : SettingsEvent()
    data class SetRingtoneVolume(val ringtoneVolume: Int) : SettingsEvent()

    object OpenEditRingtoneDuration: SettingsEvent()
    object DismissEditRingtoneDuration: SettingsEvent()
    data class SetRingtoneDuration(val ringtoneDuration: RingtoneDuration) : SettingsEvent()

    data class SetVibration(val enabled: Boolean) : SettingsEvent()

    object OpenAccountSettings : SettingsEvent()
    object OpenSoundsSettings : SettingsEvent()
    object OpenThemeSettings : SettingsEvent()
    object CloseSettingsSubsection : SettingsEvent()
    object ToggleExampleAlarmActive: SettingsEvent()

    // StorageService
    data class SaveUser(val user: User) : SettingsEvent()

    //ImageStorageService
    data class SetProfilePicture(val imageUri: Uri): SettingsEvent()

    object LogOut: SettingsEvent()
}
