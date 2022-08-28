package dev.bebora.swecker.ui.settings

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dev.bebora.swecker.MainActivity
import dev.bebora.swecker.di.AppModule
import dev.bebora.swecker.ui.theme.SweckerTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import dev.bebora.swecker.R
import dev.bebora.swecker.util.SETTINGS

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
                AnimatedNavHost(navController = navController, startDestination = SETTINGS) {
                    composable(route = SETTINGS) {
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

    @Test
    fun clickSoundsSection_SectionOpened() {
        val sounds = composeRule.activity.getString(R.string.sounds_section_title)
        val ringtone = composeRule.activity.getString(R.string.sounds_ringtone)
        composeRule.onNodeWithText(ringtone).assertDoesNotExist()
        composeRule.onNodeWithText(sounds).performClick()
        composeRule.onNodeWithText(ringtone).assertIsDisplayed()
    }

    @Test
    fun clickThemeSection_SectionOpened() {
        val theme = composeRule.activity.getString(R.string.theme_section_title)
        val palette = composeRule.activity.getString(R.string.palette)
        composeRule.onNodeWithText(palette).assertDoesNotExist()
        composeRule.onNodeWithText(theme).performClick()
        composeRule.onNodeWithText(palette).assertIsDisplayed()
    }

    @Test
    fun changeDarkMode_StateUpdated() {
        val theme = composeRule.activity.getString(R.string.theme_section_title)
        val darkMode = composeRule.activity.getString(R.string.dark_mode_dialog_title)
        val disabled = composeRule.activity.getString(R.string.dark_mode_disabled)
        val enabled = composeRule.activity.getString(R.string.dark_mode_enabled)
        composeRule.onNodeWithText(theme).performClick()
        // Set disabled
        composeRule.onNodeWithText(darkMode).performClick()
        composeRule.onNodeWithText(disabled).performClick()
        composeRule.onNodeWithText(disabled).assertIsDisplayed()
        composeRule.onNodeWithText(enabled).assertDoesNotExist()
        // Set enabled
        composeRule.onNodeWithText(darkMode).performClick()
        composeRule.onNodeWithText(enabled).performClick()
        composeRule.onNodeWithText(enabled).assertIsDisplayed()
        composeRule.onNodeWithText(disabled).assertDoesNotExist()
    }

    @Test
    fun changeRingtone_StateUpdated() {
        val sounds = composeRule.activity.getString(R.string.sounds_section_title)
        val ringtone = composeRule.activity.getString(R.string.sounds_ringtone)
        val default = composeRule.activity.getString(R.string.ringtone_default)
        val variation = composeRule.activity.getString(R.string.ringtone_variation_1)
        val confirm = composeRule.activity.getString(R.string.confirm_dialog)
        composeRule.onNodeWithText(sounds).performClick()

        // Set disabled
        composeRule.onNodeWithText(ringtone).performClick()
        composeRule.onNode(hasText(default).and(hasTestTag("popupOption"))).performClick()
        composeRule.onNode(hasText(confirm)).performClick()
        composeRule.onNode(hasText(default)).assertIsDisplayed()
        composeRule.onNodeWithText(variation).assertDoesNotExist()
        // Set enabled
        composeRule.onNodeWithText(ringtone).performClick()
        composeRule.onNodeWithText(variation).performClick()
        composeRule.onNode(hasText(confirm)).performClick()
        composeRule.onNodeWithText(variation).assertIsDisplayed()
        composeRule.onNodeWithText(default).assertDoesNotExist()
    }

    @Test
    fun changeRingtoneVolume_StateUpdated() {
        val sounds = composeRule.activity.getString(R.string.sounds_section_title)
        val volume = composeRule.activity.getString(R.string.sounds_ringtone_volume)
        val confirm = composeRule.activity.getString(R.string.confirm_dialog)
        composeRule.onNodeWithText(sounds).performClick()
        //Set maximum
        composeRule.onNodeWithText(volume).performClick()
        composeRule.onNodeWithTag("volumeSlider").performTouchInput {
            swipeRight(endX = 100000F)
        }
        composeRule.onNode(hasText(confirm)).performClick()
        composeRule.onNodeWithText("100%").assertIsDisplayed()
        //Set minimum
        composeRule.onNodeWithText(volume).performClick()
        composeRule.onNodeWithTag("volumeSlider").performTouchInput {
            swipeLeft(endX = -100000F)
        }
        composeRule.onNode(hasText(confirm)).performClick()
        composeRule.onNodeWithText("0%").assertIsDisplayed()
    }

    @Test
    fun toggleVibration_StateUpdated() {
        val sounds = composeRule.activity.getString(R.string.sounds_section_title)
        composeRule.onNodeWithText(sounds).performClick()
        // Disable vibration
        composeRule.onNodeWithTag("settingsSwitch").performTouchInput {
            swipeLeft()
        }
        composeRule.onNodeWithTag("settingsSwitch").assertIsOff()
        //Enable vibration
        composeRule.onNodeWithTag("settingsSwitch").performTouchInput {
            swipeRight()
        }
        composeRule.onNodeWithTag("settingsSwitch").assertIsOn()
    }
}
