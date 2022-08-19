package dev.bebora.swecker.ui.contact_browser

import dev.bebora.swecker.data.User

sealed class ContactsEvent {
    data class RequestFriendship(val from: User, val to: User) : ContactsEvent()
}
