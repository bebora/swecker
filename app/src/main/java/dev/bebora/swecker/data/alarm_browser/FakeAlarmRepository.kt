package dev.bebora.swecker.data.alarm_browser


import android.util.Log
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.toAlarm
import dev.bebora.swecker.data.toStoredAlarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.suspendCoroutine

class FakeAlarmRepository(fakeAlarmProviderService: AlarmProviderService) :
    AlarmRepository {
    val alarmProviderService = fakeAlarmProviderService

    override suspend fun deleteAlarm(alarm: Alarm, userId: String?) {
        suspendCoroutine { continuation ->
            alarmProviderService.deleteAlarm(alarm = alarm.toStoredAlarm().copy(userId = userId),
                onComplete = {
                    continuation.resumeWith(Result.success(Unit))
                })
        }
    }

    override suspend fun insertAlarm(alarm: Alarm, userId: String?) {
        suspendCoroutine { continuation ->
            alarmProviderService.createAlarm(alarm = alarm.toStoredAlarm().copy(userId = userId),
                onComplete = {
                    Log.d("SWECKER_CASPITO", it.toString())
                    continuation.resumeWith(Result.success(Unit))
                })
        }
        //LocalAlarmDataProvider.updateAlarm(alarm = alarm)
    }

    override suspend fun updateAlarm(alarm: Alarm, userId: String?) {
        suspendCoroutine { continuation ->
            alarmProviderService.updateAlarm(alarm = alarm.toStoredAlarm().copy(userId = userId),
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

    companion object {
        const val currentUserId = "luca"
    }
}
