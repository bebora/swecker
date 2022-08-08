package dev.bebora.swecker.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents an Alarm
 * Date is the nearest day when the alarm will ring
 */
data class Alarm(
    val id: String,
    val group: Group? = null,
    val enabled: Boolean = true,
    val name: String,
    val time: String,
    val date: String,
    val enableRepetition: Boolean = false,
    val repetitionDays: List<Day>? = null,
    val alarmType: AlarmType
)

fun alarmTypeToIcon (type: AlarmType, enabled: Boolean) :ImageVector{
    return when (type){
        AlarmType.PERSONAL -> if(enabled) Icons.Default.Person else Icons.Outlined.Person
        AlarmType.CHANNEL -> if(enabled) Icons.Default.Campaign else Icons.Outlined.Person
        AlarmType.GROUP -> if(enabled) Icons.Default.Groups else Icons.Outlined.Groups
    }
}

enum class AlarmType{
    PERSONAL,
    GROUP,
    CHANNEL
}

enum class Day{
    MON, TUE, WED, THU, FRI, SAT, SUN
}
