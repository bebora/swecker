package dev.bebora.swecker.data

import com.google.firebase.firestore.PropertyName
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
    val handle: String? = null, // Only for channels
)

data class ThinGroup( //Used for channels as well
    val id: String = "",
    val members: List<String> = emptyList(),
    val name: String = "",
    val owner: String = "",
    val picture: String = "",
    @get:PropertyName("lowerName")
    val lowerName: String? = null, // Only for channels
    val handle: String? = null // Only for channels
)

fun ThinGroup.toGroup() : Group {
    return Group(
        id = id,
        name = name,
        groupPicUrl = picture,
        firstAlarmName = "",
        firstAlarmDateTime = null,
        members = emptyList(), //TODO members in ThinGroup are string, these are User
        owner = owner,
        handle = handle
    )
}
