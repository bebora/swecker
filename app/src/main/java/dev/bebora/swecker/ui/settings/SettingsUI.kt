package dev.bebora.swecker.ui.settings

import dev.bebora.swecker.data.settings.DarkModeType

data class SettingsUI(
    val openAccountSettings: Boolean = false,
    val openSoundsSettings: Boolean = false,
    val openThemeSettings: Boolean = false,
    val exampleAlarmActive: Boolean = true,
    val showEditNamePopup: Boolean = false,
    val currentName: String = "",
    val showEditUsernamePopup: Boolean = false,
    val currentUsername: String = "",
    val showEditDarkModeTypePopup: Boolean = false,
    val currentDarkModeType: DarkModeType = DarkModeType.SYSTEM
)
