package dev.bebora.swecker.ui.alarm_browser

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
import dev.bebora.swecker.di.AppModule
import dev.bebora.swecker.ui.sweckerGraph
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.util.ALARM_BROWSER
import dev.bebora.swecker.util.TestConstants
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.testimpl.FakeAuthService

@HiltAndroidTest
@UninstallModules(AppModule::class)
class AlarmBrowserScreenTest {
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
    fun addPersonalAlarm_AlarmCreated() {
        val personalAlarmName = "Spanish lesson"
        composeRule.onNodeWithTag(TestConstants.personal).performClick()
        composeRule.onNodeWithTag(TestConstants.fab).performClick()
        val namePlaceholder = composeRule.activity.getString(R.string.name)
        composeRule.onNodeWithText(namePlaceholder).performTextInput(personalAlarmName)
        Espresso.pressBack()
        composeRule.onNodeWithTag(TestConstants.confirm).performClick()
        composeRule.onNodeWithText(personalAlarmName).assertIsDisplayed()
    }

    @Test
    fun updatePersonalAlarm_AlarmUpdated() {
        // Create alarm
        val personalAlarmName = "Spanish lesson"
        composeRule.onNodeWithTag(TestConstants.personal).performClick()
        composeRule.onNodeWithTag(TestConstants.fab).performClick()
        val namePlaceholder = composeRule.activity.getString(R.string.name)
        composeRule.onNodeWithText(namePlaceholder).performTextInput(personalAlarmName)
        Espresso.pressBack()
        composeRule.onNodeWithTag(TestConstants.confirm).performClick()
        // Update it
        composeRule.waitForIdle()
        composeRule.onNodeWithText(personalAlarmName).performClick()
        composeRule.onAllNodesWithTag(TestConstants.dayDisabled).assertCountEquals(7)
            .onFirst()
            .performClick()
        composeRule.onAllNodesWithTag(TestConstants.dayDisabled).assertCountEquals(6)
        composeRule.onNodeWithTag(TestConstants.confirm).performClick()
        composeRule.onNodeWithText(personalAlarmName).performClick()
        composeRule.onAllNodesWithTag(TestConstants.dayDisabled).assertCountEquals(6)
    }

    @Test
    fun sendMessageInChannelAlarm_MessageDisplayed() {
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
        composeRule.onNodeWithText(name).performClick()
        composeRule.onNodeWithTag(TestConstants.addChannelAlarm).performClick()
        val newChannelAlarmName = "Marzipan"
        val namePlaceholder = composeRule.activity.getString(R.string.name)
        composeRule.onNodeWithText(namePlaceholder).performTextInput(newChannelAlarmName)
        // Close keyboard and confirm
        Espresso.pressBack()
        composeRule.onNodeWithTag(TestConstants.confirm).performClick()
        // Go the new alarm
        composeRule.onNodeWithTag(TestConstants.alarmCardTime, useUnmergedTree = true).performClick()
        val chatBarPlaceholder = composeRule.activity.getString(R.string.message_placeholder)
        val newMessageText = "The best part of instrumented testing is waiting"
        // Write and send text
        composeRule.onNodeWithText(chatBarPlaceholder).performTextInput(newMessageText)
        composeRule.onNodeWithTag(TestConstants.sendMessage).performClick()
        // Text is displayed in the chat
        composeRule.onNodeWithText(newMessageText).assertIsDisplayed()
    }
}
