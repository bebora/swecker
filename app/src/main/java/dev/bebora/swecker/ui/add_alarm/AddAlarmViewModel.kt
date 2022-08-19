package dev.bebora.swecker.ui.add_alarm

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.alarm_browser.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddAlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
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
    val alarm: StateFlow<Alarm> = _alarm

    @RequiresApi(Build.VERSION_CODES.S)
    fun onUpdateCompleted(alarm: Alarm, success: Boolean) {
        if (success) {
            _alarm.value = Alarm(
                id = UUID.randomUUID().toString(),
                name = "",
                localDate = LocalDate.now(),
                localTime = LocalTime.now(),
                dateTime = OffsetDateTime.now(),
                alarmType = AlarmType.PERSONAL
            )

            CoroutineScope(Dispatchers.IO).launch {
                repository.insertAlarm(alarm)
            }
        }
    }

    fun onAlarmPartiallyUpdate(alarm: Alarm) {
        _alarm.value = alarm
    }
}
