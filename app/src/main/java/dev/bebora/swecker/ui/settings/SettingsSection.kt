package dev.bebora.swecker.ui.settings

import androidx.compose.ui.graphics.vector.ImageVector

class SettingsSection(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)