package dev.bebora.swecker.data.local

import androidx.compose.runtime.toMutableStateList
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Group
import java.time.OffsetDateTime
import java.time.ZoneId

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
            localTime = al.dateTime!!.atZoneSameInstant(ZoneId.systemDefault()).toLocalTime(),
            localDate = al.dateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDate()
        )
    }.toMutableStateList()

    val allGroups = listOf(
        Group(
            "1",
            "Wanda the group",
            members = listOf(
                "VWg6iZJh6OQDjrNFNRQ3TCAEQch2",
            ),
            firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
            owner = "@me"
        ),
        Group(
            "2",
            "Another group",
            members = emptyList(),
            firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
            owner = "@you"
        ),
        Group(
            "3",
            "A third group! Very long title",
            members = emptyList(),
            firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
            owner = "@you"
        ),
    )

    val allChannels = listOf(
        Group(
            "1",
            "Wanda the Channel",
            members = listOf(
                "VWg6iZJh6OQDjrNFNRQ3TCAEQch2",
                ),
            firstAlarmDateTime = OffsetDateTime.parse("2021-12-03T10:17:30+02:00"),
            owner = "@me",
            handle = "wandathechannel",
            groupPicUrl = "https://www.w3schools.com/html/workplace.jpg"
        ),
        Group(
            "2",
            "Another channel",
            members = emptyList(),
            firstAlarmDateTime = OffsetDateTime.parse("2021-12-03T10:11:30+02:00"),
            owner = "@you",
            handle = "examplechannel"
        ),
        Group(
            "3",
            "A third group! Very long title",
            members = emptyList(),
            firstAlarmDateTime = OffsetDateTime.parse("2021-12-09T10:15:35+02:00"),
            owner = "@you",
            handle = "thirdtimeisthecharm"
        ),
    )


    fun updateAlarm(alarm: Alarm) {
        allAlarms.replaceAll { al -> if (al.id == alarm.id) alarm else al }
    }
}
