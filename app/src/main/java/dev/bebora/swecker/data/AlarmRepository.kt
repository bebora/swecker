package dev.bebora.swecker.data

import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun insertAlarm(alarm: Alarm)

    suspend fun deleteAlarm(alarm: Alarm)

    suspend fun getAlarmById(id: String): Alarm?

    suspend fun getAlarmsByType(alarmType: AlarmType): Flow<List<Alarm>>

    suspend fun updateAlarm(alarm: Alarm)

    fun getAllAlarms(): Flow<List<Alarm>>
}
