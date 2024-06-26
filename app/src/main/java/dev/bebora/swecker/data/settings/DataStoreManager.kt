package dev.bebora.swecker.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) :
    SettingsRepositoryInterface {
    private val settingsDataStore = appContext.dataStore

    companion object {
        val PALETTE = intPreferencesKey("palette")
        val DARK_MODE_TYPE = intPreferencesKey("dark_mode_type")
        val RINGTONE = intPreferencesKey("ringtone")
        val RINGTONE_DURATION = intPreferencesKey("ringtone_duration")
        val RINGTONE_VOLUME = intPreferencesKey("ringtone_volume")
        val VIBRATION = booleanPreferencesKey("vibration")
    }

    override suspend fun setPalette(palette: Palette) {
        settingsDataStore.edit {
            it[PALETTE] = palette.ordinal
        }
    }

    override suspend fun setDarkModeType(darkModeType: DarkModeType) {
        settingsDataStore.edit {
            it[DARK_MODE_TYPE] = darkModeType.ordinal
        }
    }

    override suspend fun setRingtone(ringtone: Ringtone) {
        settingsDataStore.edit {
            it[RINGTONE] = ringtone.ordinal
        }
    }

    override suspend fun setRingtoneVolume(ringtoneVolume: Int) {
        settingsDataStore.edit {
            it[RINGTONE_VOLUME] = ringtoneVolume
        }
    }

    override suspend fun setRingtoneDuration(ringtoneDuration: RingtoneDuration) {
        settingsDataStore.edit {
            it[RINGTONE_DURATION] = ringtoneDuration.ordinal
        }
    }

    override suspend fun setVibration(enabled: Boolean) {
        settingsDataStore.edit {
            it[VIBRATION] = enabled
        }
    }

    override fun getSettings(): Flow<Settings> = settingsDataStore.data.map {
        Settings(
            palette = Palette.values()[it[PALETTE] ?: Palette.VIOLET.ordinal],
            darkModeType = DarkModeType.values()[it[DARK_MODE_TYPE] ?: DarkModeType.SYSTEM.ordinal],
            ringtone = Ringtone.values()[it[RINGTONE] ?: Ringtone.DEFAULT.ordinal],
            ringtoneDuration = RingtoneDuration.values()[it[RINGTONE_DURATION]
                ?: RingtoneDuration.SECONDS_30.ordinal],
            ringtoneVolume = it[RINGTONE_VOLUME] ?: 70,
            vibration = it[VIBRATION] ?: true,
            settingsLoaded = true
        )
    }
}
