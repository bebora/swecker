package dev.bebora.swecker.data

data class Group(
    val id: Long,
    val members: List<Contact>,
    val owner: String,
)
