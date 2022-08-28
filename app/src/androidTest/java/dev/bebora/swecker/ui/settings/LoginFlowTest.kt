package dev.bebora.swecker.ui.settings

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dev.bebora.swecker.MainActivity
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.di.AppModule
import dev.bebora.swecker.ui.login.LoginScreen
import dev.bebora.swecker.ui.sign_up.SignUpScreen
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.util.LOGIN
import dev.bebora.swecker.util.SETTINGS
import dev.bebora.swecker.util.SIGNUP
import dev.bebora.swecker.util.TestConstants
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class LoginFlowTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @OptIn(ExperimentalAnimationApi::class)
    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.activity.setContent {
            SweckerTheme {
                val navController = rememberAnimatedNavController()
                AnimatedNavHost(navController = navController, startDestination = SETTINGS) {
                    composable(SETTINGS) { backStackEntry ->
                        SettingsScreen(
                            onNavigate = { navController.navigate(it) },
                            onGoBack = {})
                    }
                    composable(LOGIN) { backStackEntry ->
                        LoginScreen(
                            onGoToSignup = { navController.navigate(SIGNUP) },
                            onGoBack = { navController.popBackStack() },
                            onLoginSuccess = { navController.popBackStack() })
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
                }
            }
        }
    }

    @Test
    fun loginFromSettings_AccountLoaded() {
        val account = composeRule.activity.getString(R.string.account_section_title)
        val login = composeRule.activity.getString(R.string.log_in_button)
        composeRule.onNodeWithText(account).performClick()
        composeRule.onNodeWithText(login).performClick()
        composeRule.onNodeWithTag(TestConstants.email).performTextInput(
            FakeAuthService.validLoginEmail
        )
        composeRule.onNodeWithTag(TestConstants.password).performTextInput(
            FakeAuthService.validPassword
        )
        composeRule.onNodeWithText(login).performClick()
        composeRule.onNodeWithContentDescription(TestConstants.logout).assertIsDisplayed()
    }

    @Test
    fun signUpFromSettings_AccountLoaded() {
        val account = composeRule.activity.getString(R.string.account_section_title)
        val login = composeRule.activity.getString(R.string.log_in_button)
        val signupProposal = composeRule.activity.getString(R.string.ask_signup)
        val signUpButton = composeRule.activity.getString(R.string.sign_up_button)
        composeRule.onNodeWithText(account).performClick()
        composeRule.onNodeWithText(login).performClick()
        composeRule.onNodeWithText(signupProposal).performClick()
        composeRule.onNodeWithTag(TestConstants.email).performTextInput(
            FakeAuthService.validSignupEmail
        )
        composeRule.onNodeWithTag(TestConstants.password).performTextInput(
            FakeAuthService.validPassword
        )
        composeRule.onNodeWithText(signUpButton).performClick()
        composeRule.onNodeWithContentDescription(TestConstants.logout).assertIsDisplayed()
    }
}
