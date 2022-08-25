package dev.bebora.swecker.data.alarm_browser


import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.toAlarm
import dev.bebora.swecker.data.toStoredAlarm
import dev.bebora.swecker.ui.alarm_browser.alarm_details.nextEnabledDate
import dev.bebora.swecker.ui.utils.onError
import kotlinx.coroutines.flow.*
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val authService: AuthService,
    private val alarmProviderService: AlarmProviderService,
) : AlarmRepository {
    private var localAlarms: Flow<List<Alarm>>? = null
    private var onlineAlarms: Flow<List<Alarm>>? = null
    private var allAlarms: Flow<List<Alarm>>? = null

    init {
        localAlarms = alarmDao.getAll()
        onlineAlarms = alarmProviderService.getUserAlarms(
            authService.getUserId()
        ).map {
            it.filter { storedAlarm ->
                !storedAlarm.deleted
            }
        }.map { alarmDataList ->
            alarmDataList.map { al ->
                al.toAlarm()
            }
        }.map { alarmList -> //Update alarms with repetition
            alarmList.map { al ->
                if (al.enabledDays.firstOrNull { b -> b } != null) {
                    al.copy(
                        dateTime = nextEnabledDate(
                            enabledDays = al.enabledDays,
                            time = al.dateTime!!.atZoneSameInstant(ZoneId.systemDefault())
                                .toLocalTime()
                        )
                    )
                } else {
                    al.copy()
                }
            }
        }.map { alarmDataList -> //add local data for the UI
            alarmDataList.map { al ->
                al.copy(
                    localTime = al.dateTime!!.atZoneSameInstant(ZoneId.systemDefault())
                        .toLocalTime(),
                    localDate = al.dateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDate()
                )
            }
        }
        allAlarms = combine(localAlarms!!, onlineAlarms!!) { local, online ->
            val onlineOnlyAlarms = online.filter { alarm ->
                val alarmSavedOffline = local.firstOrNull {
                    it.id == alarm.id
                }
                alarmSavedOffline == null
            }
            val allAlarmsSorted = local.map { localAlarm ->
                val alarmSavedOnline = online.firstOrNull {
                    it.id == localAlarm.id
                }
                //take only the most recently modified alarm
                if (alarmSavedOnline != null) {
                    if (OffsetDateTime.parse(alarmSavedOnline.timeStamp) >=
                        OffsetDateTime.parse(localAlarm.timeStamp!!)
                    ) {
                        alarmSavedOnline
                    } else {
                        localAlarm
                    }
                } else {
                    localAlarm
                }
            }.plus(onlineOnlyAlarms).sortedBy {
                it.dateTime
            }
            val beforeNow = allAlarmsSorted.filter {
                it.dateTime!! < OffsetDateTime.now()
            }
            val afterNow = allAlarmsSorted.filter {
                it.dateTime!! > OffsetDateTime.now()
            }
            afterNow.plus(beforeNow)
        }

    }

    override suspend fun deleteAlarm(alarm: Alarm, userId: String?) {
        alarmDao.delete(alarm = alarm)


        if (authService.getUserId().isNotEmpty()) {
            alarmProviderService.deleteAlarm(
                alarm = alarm.toStoredAlarm().copy(
                    userId = userId
                ),
            ) {
                if (it != null) {
                    onError(it)
                }
            }
        }
    }


    override suspend fun getAlarmById(id: String): Alarm? {
        TODO()
    }

    override suspend fun getAlarmsByType(alarmType: AlarmType): Flow<List<Alarm>> = flow {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: Alarm, userId: String?) {
        alarmDao.insert(alarm = alarm)
        if (authService.getUserId().isNotEmpty()) {
            alarmProviderService.createAlarm(
                alarm = alarm.toStoredAlarm().copy(
                    userId = userId
                )
            ) {
                if (it != null) {
                    onError(it)
                }
            }
        }
    }

    override suspend fun updateAlarm(alarm: Alarm, userId: String?) {
        alarmDao.insert(alarm = alarm)

        if (authService.getUserId().isNotEmpty()) {
            alarmProviderService.updateAlarm(
                alarm = alarm.toStoredAlarm().copy(
                    userId = userId
                )
            ) {
                if (it != null) {
                    onError(it)
                }
            }
        }
    }

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return allAlarms!!
    }
}
