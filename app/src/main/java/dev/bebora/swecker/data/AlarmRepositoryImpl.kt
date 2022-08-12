package dev.bebora.swecker.data

import dev.bebora.swecker.data.local.LocalAlarmDataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AlarmRepositoryImpl: AlarmRepository {
    override suspend fun insertAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override suspend fun getAlarmById(id: String): Alarm? {
        return LocalAlarmDataProvider.getAlarmById(id)
    }

    override suspend fun getAlarmsByType(alarmType: AlarmType): Flow<List<Alarm>> = flow {
        TODO("Not yet implemented")
    }

    override suspend fun updateAlarm(alarm: Alarm){
        TODO("Not yet implemented")
    }
    override fun getAllAlarms(): Flow<List<Alarm>> = flow {
        emit(LocalAlarmDataProvider.allAlarms)
    }
}
