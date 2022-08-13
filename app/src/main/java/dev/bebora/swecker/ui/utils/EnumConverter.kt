package dev.bebora.swecker.ui.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.bebora.swecker.R
import dev.bebora.swecker.data.settings.DarkModeType
import dev.bebora.swecker.data.settings.Palette
import dev.bebora.swecker.data.settings.Ringtone
import dev.bebora.swecker.data.settings.RingtoneDuration
import dev.bebora.swecker.ui.theme.ColorThemeWrapper
import dev.bebora.swecker.ui.theme.greenTheme
import dev.bebora.swecker.ui.theme.violetTheme
import dev.bebora.swecker.ui.theme.yellowTheme

@Composable
fun darkModeTypeToString(type: DarkModeType): String {
    return when (type) {
        DarkModeType.SYSTEM -> stringResource(R.string.dark_mode_system)
        DarkModeType.LIGHT -> stringResource(R.string.dark_mode_disabled)
        DarkModeType.DARK -> stringResource(R.string.dark_mode_enabled)
    }
}

@Composable
fun ringtoneToString(tone: Ringtone): String {
    return when (tone) {
        Ringtone.DEFAULT -> stringResource(R.string.ringtone_default)
        Ringtone.VARIATION1 -> stringResource(R.string.ringtone_variation_1)
        Ringtone.VARIATION2 -> stringResource(R.string.ringtone_variation_2)
    }
}

@Composable
fun ringtoneDurationToString(duration: RingtoneDuration): String {
    return when (duration) {
        RingtoneDuration.SECONDS_5 -> stringResource(R.string.duration_seconds_5)
        RingtoneDuration.SECONDS_30 -> stringResource(R.string.duration_seconds_30)
        RingtoneDuration.MINUTE_1 -> stringResource(R.string.duration_minute_1)
        RingtoneDuration.MINUTES_5 -> stringResource(R.string.duration_minutes_5)
    }
}

@Composable
fun useDarkPalette(type: DarkModeType): Boolean {
    return when (type) {
        DarkModeType.LIGHT -> false
        DarkModeType.DARK -> true
        DarkModeType.SYSTEM -> isSystemInDarkTheme()
    }
}

@Composable
fun paletteToColorSchemes(palette: Palette): ColorThemeWrapper {
    return when (palette) {
        Palette.SYSTEM -> ColorThemeWrapper(
            dynamicDarkColorScheme(LocalContext.current),
            dynamicLightColorScheme(LocalContext.current)
        )
        Palette.VIOLET -> violetTheme
        Palette.GREEN -> greenTheme
        Palette.YELLOW -> yellowTheme
    }
}
