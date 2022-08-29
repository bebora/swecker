package dev.bebora.swecker.ui.alarm_notification

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dev.bebora.swecker.MainActivity
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.di.AppModule
import dev.bebora.swecker.ui.sweckerGraph
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.util.ALARM_BROWSER
import dev.bebora.swecker.util.TestConstants
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class AlarmNotificationScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private var alarmDismissed = false
    private var alarmSnoozed = false

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.activity.setContent {
            SweckerTheme {
                AlarmNotificationFullScreen(
                    alarmName = "Instrumented test alarm",
                    time = "14:30",
                    onAlarmDismiss = {
                        alarmDismissed = true
                    },
                    onAlarmSnooze = {
                        alarmSnoozed = true
                    }
                )
            }
        }
    }

    @Test
    fun snoozeAlarmWithSwipe_AlarmIsSnoozed() {
        composeRule.onNodeWithTag(TestConstants.dragRingingAlarm).performTouchInput {
            swipeLeft(endX = -10000F)
        }
        assertEquals(true, alarmSnoozed)
    }

    @Test
    fun dismissAlarmWithSwipe_AlarmIsDismissed() {
        composeRule.onNodeWithTag(TestConstants.dragRingingAlarm).performTouchInput {
            swipeRight(endX = 10000F)
        }
        assertEquals(true, alarmDismissed)
    }
}
