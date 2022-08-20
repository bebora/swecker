package dev.bebora.swecker.ui.contact_browser.add_contact

import dev.bebora.swecker.data.User

sealed class AddContactEvent {
    data class QueueSearch(val query: String) : AddContactEvent()
    data class SendFriendshipRequest(val to: User) : AddContactEvent()
}
