package dev.bebora.swecker.ui.settings

import MainCoroutineRule
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.data.service.testimpl.FakeImageStorageService
import dev.bebora.swecker.data.settings.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.utils.UiText
import dev.bebora.swecker.util.UiEvent

class SettingsViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SettingsViewModel
    private val repository = FakeSettingsRepository()
    private val authService = FakeAuthService()
    private val accountsService = FakeAccountsService()
    private val imageStorageService = FakeImageStorageService()

    @Before
    fun setUp() {
        viewModel = SettingsViewModel(
            repository = repository,
            authService = authService,
            accountsService = accountsService,
            imageStorageService = imageStorageService
        )
    }

    @Test
    fun settingsViewModel_PaletteChangeEvent_ValueSaved() = runBlocking {
        Palette.values().forEach {
            viewModel.onEvent(SettingsEvent.SetPalette(it))
            assertEquals(it, viewModel.settings.first().palette)
        }
    }

    @Test
    fun settingsViewModel_DarkModeChangeEvent_ValueSaved() = runBlocking {
        DarkModeType.values().forEach {
            viewModel.onEvent(SettingsEvent.SetDarkModeType(it))
            assertEquals(it, viewModel.settings.first().darkModeType)
        }
    }

    @Test
    fun settingsViewModel_RingtoneChangeEvent_ValueSaved() = runBlocking {
        Ringtone.values().forEach {
            viewModel.onEvent(SettingsEvent.SetRingtone(it))
            assertEquals(it, viewModel.settings.first().ringtone)
        }
    }

    @Test
    fun settingsViewModel_RingtoneDurationChangeEvent_ValueSaved() = runBlocking {
        RingtoneDuration.values().forEach {
            viewModel.onEvent(SettingsEvent.SetRingtoneDuration(it))
            assertEquals(it, viewModel.settings.first().ringtoneDuration)
        }
    }

    @Test
    fun settingsViewModel_RingtoneVolumeChangeEvent_ValueSaved() = runBlocking {
        listOf(0, 40, 100).forEach {
            viewModel.onEvent(SettingsEvent.SetRingtoneVolume(it))
            assertEquals(it, viewModel.settings.first().ringtoneVolume)
        }
    }

    @Test
    fun settingsViewModel_VibrationChangeEvent_ValueSaved() = runBlocking {
        listOf(false, true).forEach {
            viewModel.onEvent(SettingsEvent.SetVibration(it))
            assertEquals(it, viewModel.settings.first().vibration)
        }
    }

    @Test
    fun settingsViewModel_OpenAndClosePopups_NotOverlapping() {
        val popupEvents = listOf(
            SettingsEvent.OpenEditDarkModeType,
            SettingsEvent.DismissEditDarkModeType,

            SettingsEvent.OpenEditName,
            SettingsEvent.DismissEditName,

            SettingsEvent.OpenEditRingtoneDuration,
            SettingsEvent.DismissEditRingtoneDuration,

            SettingsEvent.OpenEditRingtone,
            SettingsEvent.DismissEditRingtone,

            SettingsEvent.OpenEditRingtoneVolume,
            SettingsEvent.DismissEditRingtoneVolume,

            SettingsEvent.OpenEditUsername,
            SettingsEvent.DismissEditUsername,
        )
        popupEvents.forEachIndexed { index, settingsEvent ->
            viewModel.onEvent(settingsEvent)
            if (index % 2 == 0) {
                assertEquals(1, countOpenPopups())
            } else {
                assertEquals(0, countOpenPopups())
            }
        }
    }

    @Test
    fun settingsViewModel_SetTempValues_StateUpdated() {
        val tempName = "New temp name"
        val tempUsername = "adifferentusername"
        val tempRingtone = Ringtone.VARIATION1
        val tempRingtoneVolume = 100

        viewModel.onEvent(SettingsEvent.SetTempName(tempName))
        assertEquals(tempName, viewModel.uiState.currentName)

        viewModel.onEvent(SettingsEvent.SetTempUsername(tempUsername))
        assertEquals(tempUsername, viewModel.uiState.currentUsername)

        viewModel.onEvent(SettingsEvent.SetTempRingtone(tempRingtone))
        assertEquals(tempRingtone, viewModel.uiState.currentRingtone)

        viewModel.onEvent(SettingsEvent.SetTempRingtoneVolume(tempRingtoneVolume))
        assertEquals(tempRingtoneVolume, viewModel.uiState.currentRingtoneVolume)
    }

    @Test
    fun settingsViewModel_RequestLogout_UserIsLoggedOut() {
        authService.authenticate(
            FakeAuthService.validLoginEmail,
            FakeAuthService.validPassword
        ) {
            // Initial auth should be successful
            assertNull(it)
            viewModel.onEvent(SettingsEvent.LogOut)
            assertEquals("", authService.getUserId())
        }
    }

    @Test
    fun settingsViewModel_ToggleExampleAlarm_StateUpdated() {
        val initialToggleState = viewModel.uiState.exampleAlarmActive
        // State should change
        viewModel.onEvent(SettingsEvent.ToggleExampleAlarmActive)
        assertNotEquals(initialToggleState, viewModel.uiState.exampleAlarmActive)
        // State should return as the initial one
        viewModel.onEvent(SettingsEvent.ToggleExampleAlarmActive)
        assertEquals(initialToggleState, viewModel.uiState.exampleAlarmActive)
    }

    @Test
    fun settingsViewModel_OpenDummySections_NotOverlapping() {
        // Account
        viewModel.onEvent(SettingsEvent.OpenAccountSettings)
        assertEquals(true, viewModel.uiState.openAccountSettings)
        assertEquals(1, countOpenDummyScreens())
        viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
        assertEquals(0, countOpenDummyScreens())
        // Theme
        viewModel.onEvent(SettingsEvent.OpenThemeSettings)
        assertEquals(true, viewModel.uiState.openThemeSettings)
        assertEquals(1, countOpenDummyScreens())
        viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
        assertEquals(0, countOpenDummyScreens())
        // Sounds
        viewModel.onEvent(SettingsEvent.OpenSoundsSettings)
        assertEquals(true, viewModel.uiState.openSoundsSettings)
        assertEquals(1, countOpenDummyScreens())
        viewModel.onEvent(SettingsEvent.CloseSettingsSubsection)
        assertEquals(0, countOpenDummyScreens())
    }

    @Test
    fun settingsViewModel_SetAlreadyTakenUsername_UiIsNotified() =
        accountsService.saveUser(
            requestedUser = User(
                id = FakeAuthService.validUserId + "real",
                name = "Test",
                username = "handle"
            ),
            oldUser = null
        ) {
            runBlocking {
                val channel = viewModel.accountUiEvent
                viewModel.onEvent(
                    SettingsEvent.SaveUser(
                        user = User(
                            id = FakeAuthService.validUserId,
                            name = "Frech",
                            username = "handle"
                        )
                    )
                )
                assertEquals(
                    R.string.unavailable_username,
                    ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
                )
                println("Test actually done")
            }
        }

    @Test
    fun settingsViewModel_SetEmptyUsername_UiIsNotified() =
        runBlocking {
            val channel = viewModel.accountUiEvent
            viewModel.onEvent(
                SettingsEvent.SaveUser(
                    user = User(
                        id = FakeAuthService.validUserId,
                        name = "Frech",
                        username = ""
                    )
                )
            )
            assertEquals(
                R.string.blank_user_or_username,
                ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
            )
        }

    @Test
    fun settingsViewModel_SetValidUsername_StateUpdated() =
        accountsService.saveUser(
            requestedUser = User(
                id = FakeAuthService.validUserId,
                name = "Test",
                username = "handle"
            ),
            oldUser = null
        ) {
            runBlocking {
                val newName = "Totally different name"
                viewModel.onEvent(
                    SettingsEvent.SaveUser(
                        user = User(
                            id = FakeAuthService.validUserId,
                            name = newName,
                            username = "handle"
                        )
                    )
                )
                assertEquals(newName, viewModel.uiState.me.name)
                println("Test actually done")
            }
        }

    private fun countOpenPopups(): Int {
        return listOf(
            viewModel.uiState.showEditDarkModeTypePopup,
            viewModel.uiState.showEditNamePopup,
            viewModel.uiState.showEditRingtoneDurationPopup,
            viewModel.uiState.showEditRingtonePopup,
            viewModel.uiState.showEditRingtoneVolumePopup,
            viewModel.uiState.showEditUsernamePopup,
        ).fold(0) { acc, value -> acc + if (value) 1 else 0 }
    }

    private fun countOpenDummyScreens(): Int {
        return listOf(
            viewModel.uiState.openAccountSettings,
            viewModel.uiState.openThemeSettings,
            viewModel.uiState.openSoundsSettings,
        ).fold(0) { acc, value -> acc + if (value) 1 else 0 }
    }
}
