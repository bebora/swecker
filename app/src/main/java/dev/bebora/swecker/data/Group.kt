package dev.bebora.swecker.data

import java.time.OffsetDateTime

//TODO complete model for group
data class Group(
    val id: Long,
    val name: String,
    val firstAlarmName: String = "",
    val firstAlarmDateTime: OffsetDateTime? = null,
    val members: List<Contact>? = emptyList(),
    val owner: String,
)
