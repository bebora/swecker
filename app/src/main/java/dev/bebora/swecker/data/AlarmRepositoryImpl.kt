package dev.bebora.swecker.data

import dev.bebora.swecker.data.local.LocalAlarmDataProvider
import kotlinx.coroutines.flow.Flow

class AlarmRepositoryImpl: AlarmRepository {
    override suspend fun insertAlarm(alarm: Alarm) {
        LocalAlarmDataProvider.allAlarms
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override suspend fun getAlarmById(id: String): Alarm? {
        return LocalAlarmDataProvider.getAlarmById(id)
    }

    override fun getAllAlarms(): Flow<List<Alarm>> {
        TODO("Not yet implemented")
    }
}