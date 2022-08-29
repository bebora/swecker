package dev.bebora.swecker.ui.contact_browser

import MainCoroutineRule
import dev.bebora.swecker.R
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.AccountsService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.service.impl.UserWithFriends
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.ui.utils.UiText
import dev.bebora.swecker.util.UiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContactsBrowserViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ContactsBrowserViewModel
    private lateinit var authService: AuthService
    private lateinit var accountsService: AccountsService

    @Before
    fun setUp() {
        authService = FakeAuthService(initialUserId = "luca")
        accountsService = FakeAccountsService()
        viewModel = ContactsBrowserViewModel(
            authService = authService,
            accountsService = accountsService
        )
    }

    @Test
    fun contactsBrowserViewModel_AcceptFriendshipRequest_FriendAdded() = runBlocking {
        viewModel.collectorsChange.first()
        assertEquals(1, viewModel.uiState.friends.size)
        assertEquals(1, viewModel.uiState.friendshipRequests.size)
        viewModel.onEvent(
            ContactsEvent.AcceptFriendshipRequest(
                from = User(id = FakeAuthService.validUserId)
            )
        )
        assertEquals(2, accountsService.getFriends("luca").first().size)
        assertEquals(0, accountsService.getFriendshipRequests("luca").first().size)
    }

    @Test
    fun contactsBrowserViewModel_AcceptNotExistingFriendshipRequest_UiIsNotified() = runBlocking {
        viewModel.collectorsChange.first()
        val channel = viewModel.contactsUiEvent
        assertEquals(1, viewModel.uiState.friends.size)
        assertEquals(1, viewModel.uiState.friendshipRequests.size)
        viewModel.onEvent(
            ContactsEvent.AcceptFriendshipRequest(
                from = User(id = "you")
            )
        )
        assertEquals(1, accountsService.getFriends("luca").first().size)
        assertEquals(1, accountsService.getFriendshipRequests("luca").first().size)
        assertEquals(
            R.string.friendship_request_not_exists,
            ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
        )
    }

    @Test
    fun contactsBrowserViewModel_RequestFriendshipDeletion_FriendDeleted() = runBlocking {
        viewModel.collectorsChange.first()
        // carm was a friend
        assertEquals(1, viewModel.uiState.friends.size)
        viewModel.onEvent(
            ContactsEvent.RemoveFriend(
                friend = User(id = "carm")
            )
        )
        // luca has no friends
        assertEquals(0, accountsService.getFriends("luca").first().size)
    }

    @Test
    fun contactsBrowserViewModel_RemoveNotExistingUser_UiIsNotified() = runBlocking {
        viewModel.collectorsChange.first()
        val channel = viewModel.contactsUiEvent
        viewModel.onEvent(
            ContactsEvent.RemoveFriend(
                friend = User(id = "erno")
            )
        )
        assertEquals(
            R.string.invalid_user,
            ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
        )
    }
}
