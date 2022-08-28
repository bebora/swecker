package dev.bebora.swecker.ui.alarm_browser

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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

    @Test
    fun addPersonalAlarm_AlarmCreated() {
        val personanAlarmName = "Spanish lesson"
        composeRule.onNodeWithTag(TestConstants.personal).performClick()
        composeRule.onNodeWithTag(TestConstants.fab).performClick()
        val namePlaceholder = composeRule.activity.getString(R.string.name)
        composeRule.onNodeWithText(namePlaceholder).performTextInput(personanAlarmName)
        Espresso.pressBack()
        // TODO finish the test
    }
}
