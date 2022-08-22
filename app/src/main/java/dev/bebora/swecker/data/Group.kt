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

data class ThinGroup(
    val id: String = "",
    val members: List<String> = emptyList(),
    val name: String = "",
    val owner: String = "",
    val picture: String = "",
)
