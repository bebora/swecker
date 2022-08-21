package dev.bebora.swecker.data

import com.google.firebase.firestore.PropertyName

data class Message(
    val text: String = "",
    val time: Long = -1,
    @get:PropertyName("uId")
    val uId: String = "", // user id
)
