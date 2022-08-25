package dev.bebora.swecker.ui.settings

import MainCoroutineRule
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.data.service.testimpl.FakeImageStorageService
import dev.bebora.swecker.data.settings.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        viewModel = SettingsViewModel(
            repository = FakeSettingsRepository(),
            authService = FakeAuthService(),
            accountsService = FakeAccountsService(),
            imageStorageService = FakeImageStorageService()
        )
    }

    @Test
    fun settingsViewModel_PaletteChangeEvent_ValueUpdated() = runBlocking{
        Palette.values().forEach {
            viewModel.onEvent(SettingsEvent.SetPalette(it))
            assertEquals(it, viewModel.settings.first().palette)
        }
    }
    @Test
    fun settingsViewModel_DarkModeChangeEvent_ValueUpdated() = runBlocking{
        DarkModeType.values().forEach {
            viewModel.onEvent(SettingsEvent.SetDarkModeType(it))
            assertEquals(it, viewModel.settings.first().darkModeType)
        }
    }
    @Test
    fun settingsViewModel_RingtoneChangeEvent_ValueUpdated() = runBlocking{
        Ringtone.values().forEach {
            viewModel.onEvent(SettingsEvent.SetRingtone(it))
            assertEquals(it, viewModel.settings.first().ringtone)
        }
    }
    @Test
    fun settingsViewModel_RingtoneDurationChangeEvent_ValueUpdated() = runBlocking{
        RingtoneDuration.values().forEach {
            viewModel.onEvent(SettingsEvent.SetRingtoneDuration(it))
            assertEquals(it, viewModel.settings.first().ringtoneDuration)
        }
    }
    @Test
    fun settingsViewModel_RingtoneVolumeChangeEvent_ValueUpdated() = runBlocking{
        listOf(0, 40, 100).forEach {
            viewModel.onEvent(SettingsEvent.SetRingtoneVolume(it))
            assertEquals(it, viewModel.settings.first().ringtoneVolume)
        }
    }
    @Test
    fun settingsViewModel_VibrationChangeEvent_ValueUpdated() = runBlocking{
        listOf(false, true).forEach {
            viewModel.onEvent(SettingsEvent.SetVibration(it))
            assertEquals(it, viewModel.settings.first().vibration)
        }
    }
    @Test
    fun settingsViewModel_OpenAndClosePopups_NotOverlapping()  {
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
            }
            else {
                assertEquals(0, countOpenPopups())
            }
        }
    }

    private fun countOpenPopups() : Int {
        return listOf(
            viewModel.uiState.showEditDarkModeTypePopup,
            viewModel.uiState.showEditNamePopup,
            viewModel.uiState.showEditRingtoneDurationPopup,
            viewModel.uiState.showEditRingtonePopup,
            viewModel.uiState.showEditRingtoneVolumePopup,
            viewModel.uiState.showEditUsernamePopup,
        ).fold(0) { acc, value -> acc + if (value) 1 else 0 }
    }
}
