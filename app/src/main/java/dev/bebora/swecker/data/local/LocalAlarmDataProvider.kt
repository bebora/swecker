package dev.bebora.swecker.data.local

import androidx.compose.runtime.mutableStateListOf
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Contact
import dev.bebora.swecker.data.Group

object LocalAlarmDataProvider {
    val allAlarms = mutableStateListOf(
        Alarm(
            id = "@nemosi#1",
            name = "Alarm test",
            time = "14:30",
            date = "mon 7 December",
            enabledDays = listOf(true, false, false, false, true, true, true),
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
            id = "@nemosai#3",
            name = "Test!",
            time = "4:40",
            date = "mon 31 December",
            group = Group(
                1,
                "Wanda the group",
                members = listOf(
                    Contact(
                        name = "Paul",
                        tag = "@theRealPaul",
                    )
                ),
                owner = "@nemosai"
            ),
            alarmType = AlarmType.GROUP
        ),Alarm(
            id = "@nemosai#10",
            name = "Group alarms!",
            time = "4:40",
            date = "mon 31 December",
            group = Group(
                1,
                "Wanda the group",
                members = listOf(
                    Contact(
                        name = "Paul",
                        tag = "@theRealPaul",
                    )
                ),
                owner = "@nemosai"
            ),
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

    val allGroups = listOf(
        Group(
            1,
            "Wanda the group",
            members = listOf(
                Contact(
                    name = "Paul",
                    tag = "@theRealPaul",
                )
            ),
            alarms = allAlarms.filter { al -> al.alarmType == AlarmType.GROUP },
            owner = "@me"
        ),
        Group(
            2,
            "Another group",
            members = null,
            alarms = allAlarms.filter { al -> al.alarmType == AlarmType.GROUP },
            owner = "@you"
        ),
        Group(
            3,
            "A third group! Very long title",
            members = null,
            alarms = allAlarms.filter { al -> al.alarmType == AlarmType.GROUP },
            owner = "@you"
        ),
    )

    fun getAlarmById(id: String): Alarm? {
        return allAlarms.firstOrNull { al -> al.id == id }
    }

    fun updateAlarm(alarm: Alarm) {
        allAlarms.replaceAll { al -> if (al.id == alarm.id) alarm else al }
    }
}
