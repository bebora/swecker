package dev.bebora.swecker.data.alarm_browser


import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.testimpl.FakeAlarmProviderService
import dev.bebora.swecker.data.toAlarm
import dev.bebora.swecker.data.toStoredAlarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.suspendCoroutine

class FakeAlarmRepository(fakeAlarmProviderService: AlarmProviderService = FakeAlarmProviderService(), userId: String = "test") :
    AlarmRepository {
    val alarmProviderService = fakeAlarmProviderService
    val currentUserId = userId

    override suspend fun deleteAlarm(alarm: Alarm, userId: String?) {
        suspendCoroutine { continuation ->
            alarmProviderService.deleteAlarm(alarm = alarm.toStoredAlarm(),
                onComplete = {
                    continuation.resumeWith(Result.success(Unit))
                })
        }
    }

    override suspend fun insertAlarm(alarm: Alarm, userId: String?) {
        suspendCoroutine { continuation ->
            alarmProviderService.createAlarm(alarm = alarm.toStoredAlarm(),
                onComplete = {
                    continuation.resumeWith(Result.success(Unit))
                })
        }
        //LocalAlarmDataProvider.updateAlarm(alarm = alarm)
    }

    override suspend fun updateAlarm(alarm: Alarm, userId: String?) {
        suspendCoroutine { continuation ->
            alarmProviderService.updateAlarm(alarm = alarm.toStoredAlarm(),
                onComplete = {
                    continuation.resumeWith(Result.success(Unit))
                })
        }
    }

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmProviderService.getUserAlarms(currentUserId)
            .map { listStoredAlarms -> listStoredAlarms.map { it.toAlarm() } }
        //emit(LocalAlarmDataProvider.allAlarms)
    }
}
