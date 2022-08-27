package dev.bebora.swecker.ui.add_alarm


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.alarm_browser.AlarmRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddAlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val iODispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    var vmAlarm by mutableStateOf(
        Alarm(
            id = UUID.randomUUID().toString(),
            name = "",
            localDate = LocalDate.now(),
            localTime = LocalTime.now(),
            dateTime = OffsetDateTime.now(),
            alarmType = AlarmType.PERSONAL
        )
    )
        private set

    fun onUpdateCompleted(
        alarm: Alarm,
        success: Boolean,
        group: Group?,
        userId: String?,
        alarmType: AlarmType
    ) {
        if (success) {
            vmAlarm = Alarm(
                id = UUID.randomUUID().toString(),
                name = "",
                localDate = LocalDate.now(),
                localTime = LocalTime.now(),
                dateTime = OffsetDateTime.now(),
                alarmType = AlarmType.PERSONAL
            )

            CoroutineScope(iODispatcher).launch {
                repository.insertAlarm(
                    alarm.copy(
                        groupId = group?.id,
                        alarmType = alarmType,
                        timeStamp = OffsetDateTime.now()
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    ),
                    userId = if (group != null) {
                        null
                    } else {
                        userId
                    }
                )
            }
        }
        else {
            onUpdateCanceled()
        }
    }

    fun onUpdateCanceled() {
        vmAlarm = Alarm(
            id = UUID.randomUUID().toString(),
            name = "",
            localDate = LocalDate.now(),
            localTime = LocalTime.now(),
            dateTime = OffsetDateTime.now(),
            alarmType = AlarmType.PERSONAL
        )
    }

    fun onAlarmPartiallyUpdate(alarm: Alarm) {
        vmAlarm = alarm
    }
}
