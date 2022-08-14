package dev.bebora.swecker.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.bebora.swecker.LOGIN
import dev.bebora.swecker.SETTINGS
import dev.bebora.swecker.SIGNUP
import dev.bebora.swecker.SPLASH
import dev.bebora.swecker.data.settings.Settings
import dev.bebora.swecker.ui.login.LoginScreen
import dev.bebora.swecker.ui.settings.SettingsScreen
import dev.bebora.swecker.ui.settings.SettingsViewModel
import dev.bebora.swecker.ui.sign_up.SignUpScreen
import dev.bebora.swecker.ui.splash.SplashScreen
import dev.bebora.swecker.ui.theme.SettingsAwareTheme

@Composable
fun SweckerNavigation(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsState by settingsViewModel.settings.collectAsState(initial = Settings())
    SettingsAwareTheme(
        darkModeType = settingsState.darkModeType,
        palette = settingsState.palette
    ) {
        // Surface is used as a hack to prevent the screen from blinking during navigation https://stackoverflow.com/a/71889434
        Surface {
            val navController = rememberNavController()
            NavHost(navController, startDestination = SPLASH) {
                composable(SETTINGS) { backStackEntry ->
                    SettingsScreen(onNavigate = { navController.navigate(it) })
                }
                composable(LOGIN) { backStackEntry ->
                    LoginScreen(onNavigate = { navController.navigate(it) })
                }
                composable(SPLASH) {
                    SplashScreen(openAndPopUp = { route, popUp ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            popUpTo(popUp) { inclusive = true }
                        }
                    })
                }
                composable(SIGNUP) {
                    SignUpScreen(
                        openAndPopUp = { route, popUp ->
                            navController.navigate(route) {
                                launchSingleTop = true
                                popUpTo(popUp) { inclusive = true }
                            }
                        })
                }
            }
        }
    }
}
