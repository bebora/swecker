package dev.bebora.swecker.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.bebora.swecker.R
import dev.bebora.swecker.data.settings.DarkModeType

@Composable
fun darkModeTypeToString(type: DarkModeType): String {
    return when (type) {
        DarkModeType.SYSTEM -> stringResource(R.string.dark_mode_system)
        DarkModeType.LIGHT -> stringResource(R.string.dark_mode_disabled)
        DarkModeType.DARK -> stringResource(R.string.dark_mode_enabled)
    }
}
