package dev.bebora.swecker.ui.contact_browser.add_contact

import dev.bebora.swecker.data.User

data class AddContactUiState(
    val queryResults: List<User> = emptyList(),
    val me: User = User(),
    val currentQuery: String = "",
    val uploadingFriendshipRequest: Boolean = false,
    val friends: List<User> = emptyList(),
)
