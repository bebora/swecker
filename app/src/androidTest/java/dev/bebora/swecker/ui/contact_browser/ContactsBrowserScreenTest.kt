package dev.bebora.swecker.ui.contact_browser

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.dialog
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dev.bebora.swecker.MainActivity
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.di.AppModule
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserScreen
import dev.bebora.swecker.ui.login.LoginScreen
import dev.bebora.swecker.ui.sign_up.SignUpScreen
import dev.bebora.swecker.ui.sweckerGraph
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class ContactsBrowserScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
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
    fun contactBrowserNotLogged_SuggestLogin() {
        val login = composeRule.activity.getString(R.string.log_in_button)
        val openHamburger = composeRule.activity.getString(R.string.open_hamburger_menu)
        composeRule.onNodeWithContentDescription(openHamburger).performClick()
        val contacts = composeRule.activity.getString(R.string.contacts_title)
        composeRule.onNodeWithText(contacts).performClick()
        composeRule.onNodeWithText(login).assertIsDisplayed()
    }

    @Test
    fun acceptFriendshipRequest_FriendIsAdded() {
        val login = composeRule.activity.getString(R.string.log_in_button)
        val openHamburger = composeRule.activity.getString(R.string.open_hamburger_menu)
        composeRule.onNodeWithContentDescription(openHamburger).performClick()
        val contacts = composeRule.activity.getString(R.string.contacts_title)
        composeRule.onNodeWithText(contacts).performClick()
        composeRule.onNodeWithText(login).performClick()
        composeRule.onNodeWithTag(TestConstants.email).performTextInput(
            FakeAuthService.validLoginEmail
        )
        composeRule.onNodeWithTag(TestConstants.password).performTextInput(
            FakeAuthService.validPassword
        )
        composeRule.onNodeWithText(login).performClick()
        // Go back to contacts again
        composeRule.onNodeWithContentDescription(openHamburger).performClick()
        composeRule.onNodeWithText(contacts).performClick()
        // Get the original friends
        val initialFriendsCount =
            FakeAccountsService.defaultUsers[FakeAuthService.validUserId]!!.friends.size
        val initialRequestsCount =
            FakeAccountsService.defaultFriendshipRequests[FakeAuthService.validUserId]!!.size
        val friendshipRequestSection = composeRule.activity.getString(R.string.friendship_requests)
        composeRule.onNodeWithText(friendshipRequestSection).assertIsDisplayed()
        composeRule.onAllNodesWithTag(TestConstants.removeFriend, useUnmergedTree = true)
            .assertCountEquals(initialFriendsCount)
        composeRule.onAllNodesWithTag(TestConstants.acceptFriend, useUnmergedTree = true)
            .assertCountEquals(initialRequestsCount)
            .onFirst()
            .performClick()
        // I should have one more friend
        composeRule.onAllNodesWithTag(TestConstants.removeFriend, useUnmergedTree = true)
            .assertCountEquals(initialFriendsCount + 1)
    }

    @Test
    fun removeFriend_FriendIsRemoved() {
        val login = composeRule.activity.getString(R.string.log_in_button)
        val openHamburger = composeRule.activity.getString(R.string.open_hamburger_menu)
        composeRule.onNodeWithContentDescription(openHamburger).performClick()
        val contacts = composeRule.activity.getString(R.string.contacts_title)
        composeRule.onNodeWithText(contacts).performClick()
        composeRule.onNodeWithText(login).performClick()
        composeRule.onNodeWithTag(TestConstants.email).performTextInput(
            FakeAuthService.validLoginEmail
        )
        composeRule.onNodeWithTag(TestConstants.password).performTextInput(
            FakeAuthService.validPassword
        )
        composeRule.onNodeWithText(login).performClick()
        // Go back to contacts again
        composeRule.onNodeWithContentDescription(openHamburger).performClick()
        composeRule.onNodeWithText(contacts).performClick()
        // Get the original friends
        val initialFriendsCount =
            FakeAccountsService.defaultUsers[FakeAuthService.validUserId]!!.friends.size
        val friendshipRequestSection = composeRule.activity.getString(R.string.friendship_requests)
        composeRule.onNodeWithText(friendshipRequestSection).assertIsDisplayed()
        composeRule.onAllNodesWithTag(TestConstants.removeFriend, useUnmergedTree = true)
            .assertCountEquals(initialFriendsCount)
            .onFirst()
            .performClick()
        // I should have one less friend
        composeRule.onAllNodesWithTag(TestConstants.removeFriend, useUnmergedTree = true)
            .assertCountEquals(initialFriendsCount - 1)
    }
}
