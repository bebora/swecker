package dev.bebora.swecker.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.bebora.swecker.data.settings.Settings
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserScreen
import dev.bebora.swecker.ui.contact_browser.ContactBrowserScreen
import dev.bebora.swecker.ui.contact_browser.add_contact.AddContactScreen
import dev.bebora.swecker.ui.login.LoginScreen
import dev.bebora.swecker.ui.settings.SettingsScreen
import dev.bebora.swecker.ui.settings.SettingsViewModel
import dev.bebora.swecker.ui.sign_up.SignUpScreen
import dev.bebora.swecker.ui.splash.SplashScreen
import dev.bebora.swecker.ui.theme.SettingsAwareTheme
import dev.bebora.swecker.util.*

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
            NavHost(navController, startDestination = ALARM_BROWSER) {
                composable(SETTINGS) { backStackEntry ->
                    SettingsScreen(
                        onNavigate = { navController.navigate(it) },
                        onGoBack = { navController.popBackStack() })
                }
                composable(LOGIN) { backStackEntry ->
                    LoginScreen(
                        onGoToSignup = { navController.navigate(SIGNUP) },
                        onGoBack = { navController.popBackStack() },
                        onLoginSuccess = { navController.popBackStack() })
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
                        onGoToLogin = {
                            navController.navigate(LOGIN) {
                                launchSingleTop = true
                                popUpTo(SIGNUP) { inclusive = true }
                            }
                        },
                        onGoBack = { navController.popBackStack() },
                        onSignUpSuccess = { navController.popBackStack(LOGIN, inclusive = true) })
                }
                composable(ALARM_BROWSER) {
                    AlarmBrowserScreen(onNavigate = { navController.navigate(it) })
                }
                composable(CONTACT_BROWSER) {
                    ContactBrowserScreen(
                        onGoBack = { navController.popBackStack() },
                    )
                }
                composable(ADD_CONTACT) {
                    AddContactScreen(
                        onGoBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}
