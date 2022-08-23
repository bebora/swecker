package dev.bebora.swecker.data.alarm_browser

import dev.bebora.swecker.data.local.LocalAlarmDataProvider


import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AlarmRepositoryTestImpl : AlarmRepository {

    override suspend fun deleteAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override suspend fun getAlarmById(id: String): Alarm? {
        TODO()
    }

    override suspend fun getAlarmsByType(alarmType: AlarmType): Flow<List<Alarm>> = flow {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: Alarm, userId: String?) {
        LocalAlarmDataProvider.updateAlarm(alarm = alarm)
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun getAllAlarms(): Flow<List<Alarm>> = flow {
        emit(LocalAlarmDataProvider.allAlarms)
    }
}
