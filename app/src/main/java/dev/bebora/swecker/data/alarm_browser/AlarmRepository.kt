package dev.bebora.swecker.data.alarm_browser

import dev.bebora.swecker.data.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun deleteAlarm(alarm: Alarm, userId: String?)

    suspend fun insertAlarm(alarm: Alarm, userId: String?)

    suspend fun updateAlarm(alarm: Alarm, userId: String?)

    fun getAllAlarms(): Flow<List<Alarm>>
}
