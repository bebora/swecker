package dev.bebora.swecker.ui.contact_browser.add_contact

import dev.bebora.swecker.data.User

data class AddContactUiState(
    val queryResults: List<User> = emptyList(),
    val me: User = User(),
    val currentQuery: String = "",
    val uploadingFriendshipRequest: Boolean = false,
    val friendsIds: Set<String> = emptySet(),
    val processingQuery: Boolean = false,
    val accountStatusLoaded: Boolean = false
)
