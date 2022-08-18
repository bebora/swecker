package dev.bebora.swecker.data.alarm_browser

import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    override suspend fun deleteAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override suspend fun getAlarmById(id: String): Alarm? {
        TODO()
    }

    override suspend fun getAlarmsByType(alarmType: AlarmType): Flow<List<Alarm>> = flow {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: Alarm) {
        alarmDao.insert(alarm = alarm)
    }

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAll()
    }
}
