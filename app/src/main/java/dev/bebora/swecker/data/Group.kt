package dev.bebora.swecker.data

//TODO complete model for group
data class Group(
    val id: Long,
    val name: String,
    val alarms: List<Alarm>,
    val members: List<Contact>?,
    val owner: String,
)
