package dev.bebora.swecker.data.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SettingsRepositoryPreview : SettingsRepositoryInterface {
    override suspend fun setName(name: String) {
    }

    override suspend fun setUsername(username: String) {
    }

    override suspend fun setPalette(palette: Palette) {
    }

    override suspend fun setDarkModeType(darkModeType: DarkModeType) {
    }

    override suspend fun setRingtone(ringtone: Ringtone) {
    }

    override suspend fun setRingtoneVolume(ringtoneVolume: Int) {
    }

    override suspend fun setRingtoneDuration(ringtoneDuration: RingtoneDuration) {
    }

    override suspend fun setVibration(enabled: Boolean) {
    }

    override fun getSettings(): Flow<Settings> {
        val previewSettings = Settings(
            name = "Example",
            username = "@me",
            palette = Palette.VIOLET,
            darkModeType = DarkModeType.SYSTEM,
            ringtone = Ringtone.DEFAULT,
            ringtoneDuration = RingtoneDuration.SECONDS_5,
            ringtoneVolume = 50,
            vibration = true
        )

        return flow {
            while(true) {
                emit(previewSettings)
            }
        }
    }
}
