package dev.bebora.swecker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import dev.bebora.swecker.data.settings.DarkModeType
import dev.bebora.swecker.data.settings.Palette
import dev.bebora.swecker.ui.utils.paletteToColorSchemes
import dev.bebora.swecker.ui.utils.useDarkPalette


@Composable
fun SweckerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    colorSchemes: ColorThemeWrapper = violetTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> colorSchemes.darkColorScheme
        else -> colorSchemes.lightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Select the correct theme from user settings
 */
@Composable
fun SettingsAwareTheme(
    darkModeType: DarkModeType,
    palette: Palette,
    content: @Composable () -> Unit
) {
    val darkMode = useDarkPalette(type = darkModeType)
    
    val dynamicColor = palette == Palette.SYSTEM

    SweckerTheme(
        darkTheme = darkMode,
        dynamicColor = dynamicColor,
        colorSchemes = paletteToColorSchemes(palette = palette),
        content = content
    )
}
