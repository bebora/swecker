package dev.bebora.swecker.data.local

import androidx.compose.runtime.toMutableStateList
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Group
import java.time.OffsetDateTime

object LocalAlarmDataProvider {
    val allAlarms = listOf(
        Alarm(
            id = "@nemosi#1",
            name = "Alarm test",
            dateTime = OffsetDateTime.parse("2011-12-03T10:15:30+01:00"),
            enabledDays = listOf(true, false, false, false, true, true, true),
            alarmType = AlarmType.PERSONAL
        ),
        Alarm(
            id = "@nemosi#2",
            enabled = false,
            name = "Alarm test 2, very long lable",
            dateTime = OffsetDateTime.parse("2011-12-03T10:18:30+01:00"),
            alarmType = AlarmType.PERSONAL
        ),
        Alarm(
            id = "@nemosai#3",
            groupId = "wandaId",
            name = "Test!",
            dateTime = OffsetDateTime.parse("2011-12-03T10:15:30+01:00"),
            alarmType = AlarmType.GROUP
        ), Alarm(
            id = "@nemosai#10",
            groupId = "wandaId",
            name = "Group alarms!",
            dateTime = OffsetDateTime.parse("2018-11-03T10:15:30+01:00"),
            alarmType = AlarmType.GROUP
        ), Alarm(
            id = "@nemosi#4",
            name = "Stop the oven",
            dateTime = OffsetDateTime.parse("2022-12-03T10:10:30+01:00"),
            alarmType = AlarmType.PERSONAL
        ),
        Alarm(
            id = "@nemosi#5",
            enabled = false,
            name = "Alarm test 2, very long lable",
            dateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
            alarmType = AlarmType.PERSONAL
        ),
        Alarm(
            id = "@nemosi#7",
            name = "Footbal match",
            dateTime = OffsetDateTime.parse("2011-02-12T10:15:30+01:00"),
            alarmType = AlarmType.CHANNEL
        )
    ).map { al ->
        al.copy(
            localTime = al.dateTime!!.toLocalTime(),
            localDate = al.dateTime.toLocalDate()
        )
    }.toMutableStateList()

    val allGroups = listOf(
        Group(
            "1",
            "Wanda the group",
            members = listOf(
            ),
            firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
            owner = "@me"
        ),
        Group(
            "2",
            "Another group",
            members = null,
            firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
            owner = "@you"
        ),
        Group(
            "3",
            "A third group! Very long title",
            members = null,
            firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
            owner = "@you"
        ),
    )


    fun updateAlarm(alarm: Alarm) {
        allAlarms.replaceAll { al -> if (al.id == alarm.id) alarm else al }
    }
}
