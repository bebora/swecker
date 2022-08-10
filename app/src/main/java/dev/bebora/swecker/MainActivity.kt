package dev.bebora.swecker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import dev.bebora.swecker.ui.settings.SettingsScreen
import dev.bebora.swecker.ui.theme.SweckerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SweckerTheme {
                SettingsScreen()
            }
        }
    }
}
