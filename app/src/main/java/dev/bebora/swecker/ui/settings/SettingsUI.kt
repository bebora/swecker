package dev.bebora.swecker.ui.settings

data class SettingsUI(
    val openAccountSettings: Boolean = false,
    val openSoundsSettings: Boolean = false,
    val openThemeSettings: Boolean = false,
    val exampleAlarmActive: Boolean = true,
    val showEditNamePopup: Boolean = false,
    val currentName: String = "",
    val showEditUsernamePopup: Boolean = false,
    val currentUsername: String = ""
)
