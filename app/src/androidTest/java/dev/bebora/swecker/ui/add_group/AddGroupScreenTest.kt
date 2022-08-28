package dev.bebora.swecker.ui.add_group

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
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.di.AppModule
import dev.bebora.swecker.ui.sweckerGraph
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.util.ALARM_BROWSER
import dev.bebora.swecker.util.TestConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
@UninstallModules(AppModule::class)
class AddGroupScreenTest {
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
    fun openAddGroupNotLogged_SuggestLogin() {
        val login = composeRule.activity.getString(R.string.log_in_button)
        composeRule.onNodeWithTag(TestConstants.groups).performClick()
        composeRule.onNodeWithTag(TestConstants.fab).performClick()
        composeRule.onNodeWithText(login).assertIsDisplayed()
    }

    @Test
    fun openAddGroupFromFABLogged_DialogOpened() {
        // Initial login
        val login = composeRule.activity.getString(R.string.log_in_button)
        composeRule.onNodeWithTag(TestConstants.groups).performClick()
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
        val searchContacts = composeRule.activity.getString(R.string.search_contacts)
        composeRule.onNodeWithTag(TestConstants.groups).performClick()
        composeRule.onNodeWithTag(TestConstants.fab).performClick()
        composeRule.onNodeWithText(searchContacts).assertIsDisplayed()
    }

    @Test
    fun createGroup_GroupCreated() {
        // Initial login
        val login = composeRule.activity.getString(R.string.log_in_button)
        composeRule.onNodeWithTag(TestConstants.groups).performClick()
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
        val searchBarPlaceholder = composeRule.activity.getString(R.string.search_contacts)
        composeRule.onNodeWithTag(TestConstants.groups).performClick()
        composeRule.onNodeWithTag(TestConstants.fab).performClick()
        composeRule.onNodeWithText(searchBarPlaceholder).assertIsDisplayed()
        // Select friend
        composeRule.onNodeWithText(FakeAccountsService.friendName)
            .assertIsDisplayed()
            .performClick()

        composeRule.onNodeWithTag(TestConstants.proceed).performClick()
        // Create group
        val name = "Jemangohoist"

        composeRule.onNodeWithTag(TestConstants.name).performTextInput(
            name
        )
        // Close keyboard and confirm
        Espresso.pressBack()
        composeRule.onNodeWithTag(TestConstants.confirm).performClick()
        // Group is displayed
        runBlocking { delay(1500) }
        composeRule.onNodeWithText(name).assertIsDisplayed()
    }
}
