package dev.bebora.swecker.ui.settings

import dev.bebora.swecker.data.settings.Ringtone
import dev.bebora.swecker.data.settings.RingtoneDuration

data class SettingsUiState(
    val openAccountSettings: Boolean = false,
    val openSoundsSettings: Boolean = false,
    val openThemeSettings: Boolean = false,
    val exampleAlarmActive: Boolean = true,

    val showEditNamePopup: Boolean = false,
    val currentName: String = "",

    val showEditUsernamePopup: Boolean = false,
    val currentUsername: String = "",

    val showEditDarkModeTypePopup: Boolean = false,

    val showEditRingtonePopup: Boolean = false,
    val currentRingtone: Ringtone = Ringtone.DEFAULT,

    val showEditRingtoneDurationPopup: Boolean = false,
    val currentRingtoneDuration: RingtoneDuration = RingtoneDuration.SECONDS_5,

    val showEditRingtoneVolumePopup: Boolean = false,
    val currentRingtoneVolume: Int = 0,

    val hasUser: Boolean = false,
    val userId: String = "",

    val savedName: String = "",
    val savedUsername: String = ""
)
