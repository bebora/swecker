package dev.bebora.swecker.data.alarm_browser

import dev.bebora.swecker.data.local.LocalAlarmDataProvider


import dev.bebora.swecker.data.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PreviewAlarmRepository : AlarmRepository {

    override suspend fun deleteAlarm(alarm: Alarm,userId: String?) {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: Alarm, userId: String?) {
        LocalAlarmDataProvider.updateAlarm(alarm = alarm)
    }

    override suspend fun updateAlarm(alarm: Alarm, userId: String?) {
        TODO("Not yet implemented")
    }

    override fun getAllAlarms(): Flow<List<Alarm>> = flow {
        emit(LocalAlarmDataProvider.allAlarms)
    }
}
