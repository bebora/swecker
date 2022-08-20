package dev.bebora.swecker.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SweckerNavigation(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    // Find a way to have a background color darker than white
    val settingsState by settingsViewModel.settings.collectAsState(initial = Settings(settingsLoaded = false))
    if (!settingsState.settingsLoaded) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text(text = "Swecker") }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        }
    } else {
        SettingsAwareTheme(
            darkModeType = settingsState.darkModeType,
            palette = settingsState.palette
        ) {
            // Surface is used as a hack to prevent the screen from blinking during navigation https://stackoverflow.com/a/71889434
            Surface {
                val navController = rememberAnimatedNavController()
                AnimatedNavHost(navController, startDestination = ALARM_BROWSER,
                    enterTransition = {
                        slideInHorizontally(initialOffsetX = { it / 2 }) + scaleIn(initialScale = .8f)
                    },
                    exitTransition = {
                        slideOutHorizontally(targetOffsetX = { -it / 2 }) + scaleOut(targetScale = .8f)
                    },
                    popEnterTransition = {
                        slideInHorizontally(initialOffsetX = { -it / 2 }) + scaleIn(initialScale = .8f)
                    }
                ) {
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
                            onSignUpSuccess = {
                                navController.popBackStack(
                                    LOGIN,
                                    inclusive = true
                                )
                            })
                    }
                    composable(ALARM_BROWSER) {
                        AlarmBrowserScreen(onNavigate = { navController.navigate(it) })
                    }
                    composable(CONTACT_BROWSER) {
                        ContactBrowserScreen(
                            onNavigate = { navController.navigate(it) },
                            onGoBack = { navController.popBackStack() },
                        )
                    }
                    composable(ADD_CONTACT) {
                        AddContactScreen(
                            onGoBack = { navController.popBackStack() },
                            onNavigate = { navController.navigate(it) }
                        )
                    }
                }
            }
        }
    }
}
