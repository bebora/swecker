package dev.bebora.swecker.ui.contact_browser

import dev.bebora.swecker.data.User

data class ContactsUiState(
    val friends: List<User> = emptyList(),
    val uploadingFriendshipRequest: Boolean = false,
    val me: User = User()
)
