package dev.bebora.swecker.data.settings

import kotlinx.coroutines.flow.Flow

enum class DarkModeType {
    SYSTEM,
    LIGHT,
    DARK
}

enum class Ringtone {
    DEFAULT,
    VARIATION1,
    VARIATION2
}

enum class Palette {
    SYSTEM,
    VARIATION1,
    VARIATION2,
    VARIATION3
}

enum class RingtoneDuration {
    SECONDS_5,
    SECONDS_30,
    MINUTE_1,
    MINUTES_5
}

data class Settings(
    val name: String = "Example",
    val username: String = "@me",
    val palette: Palette = Palette.SYSTEM,
    val darkModeType: DarkModeType = DarkModeType.SYSTEM,
    val ringtone: Ringtone = Ringtone.DEFAULT,
    val ringtoneVolume: Int = 50,
    val ringtoneDuration: RingtoneDuration = RingtoneDuration.SECONDS_5,
    val vibration: Boolean = true
)

interface SettingsRepositoryInterface {
    suspend fun setName(name: String)
    suspend fun setUsername(username: String)
    suspend fun setPalette(palette: Palette)
    suspend fun setDarkModeType(darkModeType: DarkModeType)
    suspend fun setRingtone(ringtone: Ringtone)
    suspend fun setRingtoneVolume(ringtoneVolume: Int)
    suspend fun setRingtoneDuration(ringtoneDuration: RingtoneDuration)
    suspend fun toggleVibration()

    fun getSettings(): Flow<Settings>
}
