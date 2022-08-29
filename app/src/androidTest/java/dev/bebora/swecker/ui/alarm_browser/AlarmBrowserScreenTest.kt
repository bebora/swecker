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

    /*@Test
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
        runBlocking { delay(1000) }
        composeRule.onNodeWithText(personalAlarmName).performClick()
        composeRule.onAllNodesWithTag(TestConstants.dayDisabled).assertCountEquals(7)
            .onFirst()
            .performClick()
        composeRule.onAllNodesWithTag(TestConstants.dayDisabled).assertCountEquals(6)
        composeRule.onNodeWithTag(TestConstants.confirm).performClick()
        composeRule.onNodeWithText(personalAlarmName).performClick()
        composeRule.onAllNodesWithTag(TestConstants.dayDisabled).assertCountEquals(6)
    }*/
}
