package dev.bebora.swecker.data.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class FakeSettingsRepository : SettingsRepositoryInterface {
    private var fakeSettings = fakeSettingsDefault

    private val settingsFlow = MutableStateFlow(fakeSettings)


    override suspend fun setPalette(palette: Palette) {
        fakeSettings = fakeSettings.copy(
            palette = palette
        )
        settingsFlow.value = fakeSettings
    }

    override suspend fun setDarkModeType(darkModeType: DarkModeType) {
        fakeSettings = fakeSettings.copy(
            darkModeType = darkModeType
        )
        settingsFlow.value = fakeSettings
    }

    override suspend fun setRingtone(ringtone: Ringtone) {
        fakeSettings = fakeSettings.copy(
            ringtone = ringtone
        )
        settingsFlow.value = fakeSettings
    }

    override suspend fun setRingtoneVolume(ringtoneVolume: Int) {
        fakeSettings = fakeSettings.copy(
            ringtoneVolume = ringtoneVolume
        )
        settingsFlow.value = fakeSettings
    }

    override suspend fun setRingtoneDuration(ringtoneDuration: RingtoneDuration) {
        fakeSettings = fakeSettings.copy(
            ringtoneDuration = ringtoneDuration
        )
        settingsFlow.value = fakeSettings
    }

    override suspend fun setVibration(enabled: Boolean) {
        fakeSettings = fakeSettings.copy(
            vibration = enabled
        )
        settingsFlow.value = fakeSettings
    }

    override fun getSettings(): Flow<Settings> {
        return settingsFlow.asStateFlow()
    }

    companion object {
        val fakeSettingsDefault = Settings(
            palette = Palette.VIOLET,
            darkModeType = DarkModeType.SYSTEM,
            ringtone = Ringtone.DEFAULT,
            ringtoneDuration = RingtoneDuration.SECONDS_5,
            ringtoneVolume = 50,
            vibration = true
        )
    }
}
