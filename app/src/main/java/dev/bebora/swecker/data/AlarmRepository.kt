package dev.bebora.swecker.data

import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun insertAlarm(alarm: Alarm)

    suspend fun deleteAlarm(alarm: Alarm)

    suspend fun getAlarmById(id: String): Alarm?

    fun getAllAlarms(): Flow<List<Alarm>>
}