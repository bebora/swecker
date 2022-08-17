package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.ui.graphics.vector.ImageVector

class DrawerSubSection(
    val title: String,
    val icon: ImageVector,
    val selected: Boolean = false,
    val onClick: () -> Unit = {}
)

class DrawerSection(
    val title: String,
    val subsections: List<DrawerSubSection> = emptyList()
)
