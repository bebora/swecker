package dev.bebora.swecker.ui.add_alarm


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.alarm_browser.AlarmRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _alarm = MutableStateFlow(
        Alarm(
            id = UUID.randomUUID().toString(),
            name = "",
            localDate = LocalDate.now(),
            localTime = LocalTime.now(),
            dateTime = OffsetDateTime.now(),
            alarmType = AlarmType.PERSONAL
        )
    )
    val alarm: StateFlow<Alarm> = _alarm.asStateFlow()

    fun onUpdateCompleted(alarm: Alarm, success: Boolean, group: Group?, userId: String?, alarmType: AlarmType) {
        if (success) {
            _alarm.value = Alarm(
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
    }

    fun onUpdateCanceled() {
        _alarm.value = Alarm(
            id = UUID.randomUUID().toString(),
            name = "",
            localDate = LocalDate.now(),
            localTime = LocalTime.now(),
            dateTime = OffsetDateTime.now(),
            alarmType = AlarmType.PERSONAL
        )
    }

    fun onAlarmPartiallyUpdate(alarm: Alarm) {
        _alarm.value = alarm
    }
}
