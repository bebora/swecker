package dev.bebora.swecker.data.local

import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType

object LocalAlarmDataProvider {
    val allAlarms = listOf(
        Alarm(
            id = "@nemosi#1",
            name = "Alarm test",
            time = "14:30",
            date = "mon 7 December",
            alarmType = AlarmType.PERSONAL
        ),
        Alarm(
            id = "@nemosi#2",
            enabled = false,
            name = "Alarm test 2, very long lable",
            time = "15:30",
            date = "mon 9 December",
            alarmType = AlarmType.PERSONAL
        ),
        Alarm(
            id = "@nemosi#3",
            name = "Test!",
            time = "4:40",
            date = "mon 31 December",
            alarmType = AlarmType.GROUP
        ), Alarm(
            id = "@nemosi#4",
            name = "Stop the oven",
            time = "14:30",
            date = "mon 7 October",
            alarmType = AlarmType.PERSONAL
        ),
        Alarm(
            id = "@nemosi#5",
            enabled = false,
            name = "Alarm test 2, very long lable",
            time = "15:30",
            date = "mon 9 December",
            alarmType = AlarmType.PERSONAL
        ),
        Alarm(
            id = "@nemosi#7",
            name = "Footbal match",
            time = "15:40",
            date = "wed 7 December",
            alarmType = AlarmType.CHANNEL
        )

    )

    fun getAlarmById(id: String): Alarm? {
        return allAlarms.firstOrNull { al -> al.id == id }
    }
}
