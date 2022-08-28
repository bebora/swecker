package dev.bebora.swecker.ui.add_channel

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dev.bebora.swecker.MainActivity
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.di.AppModule
import dev.bebora.swecker.ui.sweckerGraph
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.util.ALARM_BROWSER
import dev.bebora.swecker.util.TestConstants
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class AddChannelScreenTest {
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
                AnimatedNavHost(navController = navController, startDestination = ALARM_BROWSER) {
                    sweckerGraph(navController = navController)
                }
            }
        }
    }

    @Test
    fun openAddChannelNotLogged_SuggestLogin() {
        val login = composeRule.activity.getString(R.string.log_in_button)
        composeRule.onNodeWithTag(TestConstants.channels).performClick()
        composeRule.onNodeWithTag(TestConstants.fab).performClick()
        composeRule.onNodeWithText(login).assertIsDisplayed()
    }

    @Test
    fun openAddChannelFromFABLogged_DialogOpened() {
        // Initial login
        val login = composeRule.activity.getString(R.string.log_in_button)
        composeRule.onNodeWithTag(TestConstants.channels).performClick()
        composeRule.onNodeWithTag(TestConstants.fab).performClick()
        composeRule.onNodeWithText(login).performClick()
        composeRule.onNodeWithTag(TestConstants.email).performTextInput(
            FakeAuthService.validLoginEmail
        )
        composeRule.onNodeWithTag(TestConstants.password).performTextInput(
            FakeAuthService.validPassword
        )
        composeRule.onNodeWithText(login).performClick()
        // Open dialog with text fields
        val handle = composeRule.activity.getString(R.string.channel_handle)
        composeRule.onNodeWithTag(TestConstants.channels).performClick()
        composeRule.onNodeWithTag(TestConstants.fab).performClick()
        composeRule.onNodeWithText(handle).assertIsDisplayed()
    }

    @Test
    fun createChannel_ChannelCreated() {
        // Initial login
        val login = composeRule.activity.getString(R.string.log_in_button)
        composeRule.onNodeWithTag(TestConstants.channels).performClick()
        composeRule.onNodeWithTag(TestConstants.fab).performClick()
        composeRule.onNodeWithText(login).performClick()
        composeRule.onNodeWithTag(TestConstants.email).performTextInput(
            FakeAuthService.validLoginEmail
        )
        composeRule.onNodeWithTag(TestConstants.password).performTextInput(
            FakeAuthService.validPassword
        )
        composeRule.onNodeWithText(login).performClick()
        // Open dialog with text fields
        val handlePlaceholder = composeRule.activity.getString(R.string.channel_handle)
        composeRule.onNodeWithTag(TestConstants.channels).performClick()
        composeRule.onNodeWithTag(TestConstants.fab).performClick()
        composeRule.onNodeWithText(handlePlaceholder).assertIsDisplayed()
        // Create channel
        val name = "Klamitos"
        val handle = "speros"
        composeRule.onNodeWithTag(TestConstants.name).performTextInput(
            name
        )
        composeRule.onNodeWithTag(TestConstants.handle).performTextInput(
            handle
        )
        // Close keyboard and confirm
        Espresso.pressBack()
        composeRule.onNodeWithTag(TestConstants.confirm).performClick()
        composeRule.onNodeWithText(name).assertIsDisplayed()
    }
}
