package dev.bebora.swecker.data.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSettingsRepository : SettingsRepositoryInterface {
    private var fakeSettings = fakeSettingsDefault

    override suspend fun setPalette(palette: Palette) {
        fakeSettings = fakeSettings.copy(
            palette = palette
        )
    }

    override suspend fun setDarkModeType(darkModeType: DarkModeType) {
        fakeSettings = fakeSettings.copy(
            darkModeType = darkModeType
        )
    }

    override suspend fun setRingtone(ringtone: Ringtone) {
        fakeSettings = fakeSettings.copy(
            ringtone = ringtone
        )
    }

    override suspend fun setRingtoneVolume(ringtoneVolume: Int) {
        fakeSettings = fakeSettings.copy(
            ringtoneVolume = ringtoneVolume
        )
    }

    override suspend fun setRingtoneDuration(ringtoneDuration: RingtoneDuration) {
        fakeSettings = fakeSettings.copy(
            ringtoneDuration = ringtoneDuration
        )
    }

    override suspend fun setVibration(enabled: Boolean) {
        fakeSettings = fakeSettings.copy(
            vibration = enabled
        )
    }

    override fun getSettings(): Flow<Settings> {
        return flow {
            emit(fakeSettings)
        }
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
