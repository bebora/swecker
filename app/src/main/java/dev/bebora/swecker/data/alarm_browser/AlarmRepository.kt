package dev.bebora.swecker.data.alarm_browser

import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun deleteAlarm(alarm: Alarm, userId: String?)

    suspend fun getAlarmById(id: String): Alarm?

    suspend fun getAlarmsByType(alarmType: AlarmType): Flow<List<Alarm>>

    suspend fun insertAlarm(alarm: Alarm, userId: String?)

    suspend fun updateAlarm(alarm: Alarm, userId: String?)

    fun getAllAlarms(): Flow<List<Alarm>>
}
