package dev.bebora.swecker.ui.settings

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dev.bebora.swecker.MainActivity
import dev.bebora.swecker.di.AppModule
import dev.bebora.swecker.ui.theme.SweckerTheme
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import dev.bebora.swecker.R

@HiltAndroidTest
@UninstallModules(AppModule::class)
class SettingsScreenTest {
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

                AnimatedNavHost(navController = navController, startDestination = "SETTINGS") {
                    composable(route = "SETTINGS") {
                        SettingsScreen()
                    }
                }
            }
        }
    }

    @Test
    fun clickAccountSection_SectionOpened() {
        val account = composeRule.activity.getString(R.string.account_section_title)
        val login = composeRule.activity.getString(R.string.log_in_button)
        composeRule.onNodeWithText(login).assertDoesNotExist()
        composeRule.onNodeWithText(account).performClick()
        composeRule.onNodeWithText(login).assertIsDisplayed()
    }
}
