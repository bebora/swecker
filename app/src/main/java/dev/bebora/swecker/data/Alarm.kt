package dev.bebora.swecker.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import dev.bebora.swecker.ui.alarm_browser.NavBarDestination
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Represents an Alarm
 * Date is the nearest day when the alarm will ring
 */
@Entity()
data class Alarm(
    @PrimaryKey val id: String = "",
    val groupId: String? = null,
    val enabled: Boolean = true,
    val name: String,
    val localDate: LocalDate? = null,
    val localTime: LocalTime? = null,
    val dateTime: OffsetDateTime? = null,
    val enableChat: Boolean = true,
    val enabledDays: List<Boolean> = listOf(false, false, false, false, false, false, false),
    val alarmType: AlarmType,
    val timeStamp: String? = null
)

fun alarmTypeToIcon(type: AlarmType, enabled: Boolean): ImageVector {
    return when (type) {
        AlarmType.PERSONAL -> if (enabled) Icons.Default.Person else Icons.Outlined.Person
        AlarmType.CHANNEL -> if (enabled) Icons.Default.Campaign else Icons.Outlined.Campaign
        AlarmType.GROUP -> if (enabled) Icons.Default.Groups else Icons.Outlined.Groups
    }
}

enum class AlarmType {
    PERSONAL,
    GROUP,
    CHANNEL
}

fun alarmTypeFromNavbarDestination(destination: NavBarDestination):AlarmType{
    return when (destination){
        NavBarDestination.PERSONAL,NavBarDestination.HOME -> AlarmType.PERSONAL
        NavBarDestination.CHANNELS -> AlarmType.CHANNEL
        NavBarDestination.GROUPS -> AlarmType.GROUP
    }
}

fun AlarmType.toStoredString() : String {
    return when (this) {
        AlarmType.PERSONAL -> "personal"
        AlarmType.GROUP -> "group"
        AlarmType.CHANNEL -> "channel"
    }
}

fun String.toAlarmType() : AlarmType {
    return when (this) {
        "group" -> AlarmType.GROUP
        "channel" -> AlarmType.CHANNEL
        else -> AlarmType.PERSONAL
    }
}

data class StoredAlarm(
    val id: String = "", // Original id. In groups, multiple documents will have the same id field (but a unique document id)
    val enabled: Boolean = true, // Note that if false, user will not receive future updates from groups
    @get:PropertyName("userId")
    val userId: String? = null,
    @get:PropertyName("groupId")
    val groupId: String? = null,
    val name: String = "",
    @get:PropertyName("enableChat")
    val enableChat: Boolean = true,
    @get:PropertyName("enabledDays")
    val enabledDays: String = "0000000",
    @get:PropertyName("alarmType")
    val alarmType: String = "personal",
    val timestamp: String? = null, //Choose precision of timestamp
    @get:PropertyName("dateTime")
    val dateTime: String = "2022-08-25T16:05:00+01:00",
    val deleted: Boolean = false,
    @get:PropertyName("receiveUpdates")
    val receiveUpdates: Boolean = true // If true get updates from original alarm (unless the alarm has been disabled)
)

fun Alarm.toStoredAlarm() : StoredAlarm {
    return StoredAlarm(
        id = id,
        enabled = enabled,
        groupId = groupId,
        name = name,
        enableChat = enableChat,
        enabledDays = enabledDaysToString(enabledDays),
        alarmType = alarmType.toStoredString(),
        timestamp = timeStamp,
        dateTime = dateTime?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: "2022-08-25T16:05:00+01:00"
    )
}

fun StoredAlarm.toAlarm() : Alarm {
    return Alarm(
        id = id,
        enabled = enabled,
        groupId = groupId,
        name = name,
        enableChat = enableChat,
        enabledDays = enabledDaysToList(enabledDays),
        alarmType = alarmType.toAlarmType(),
        timeStamp = timestamp,
        dateTime = OffsetDateTime.parse(dateTime)
    )
}

fun enabledDaysToString(daysAsList: List<Boolean>) : String {
    return daysAsList.joinToString(separator = "") { if (it) "1" else "0" }
}

fun enabledDaysToList(daysAsString: String) : List<Boolean> {
    return daysAsString.map { it == '1' }
}
