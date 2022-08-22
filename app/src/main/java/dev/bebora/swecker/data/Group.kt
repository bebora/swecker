package dev.bebora.swecker.data

import java.time.OffsetDateTime

//TODO complete model for group
data class Group(
    val id: String,
    val name: String,
    val groupPicUrl: String = "",
    val firstAlarmName: String = "",
    val firstAlarmDateTime: OffsetDateTime? = null,
    val members: List<User> = emptyList(),
    val owner: String,
)
