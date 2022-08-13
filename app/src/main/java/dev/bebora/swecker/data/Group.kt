package dev.bebora.swecker.data

//TODO complete model for group
data class Group(
    val id: Long,
    val name: String,
    val alarms: List<Alarm> = emptyList(),
    val members: List<Contact>? = emptyList(),
    val owner: String,
)
