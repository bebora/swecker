package dev.bebora.swecker.ui.contact_browser.add_contact

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
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddContactViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AddContactViewModel
    private lateinit var authService: AuthService
    private lateinit var accountsService: AccountsService

    @OptIn(ExperimentalCoroutinesApi::class)
    val dispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        authService = FakeAuthService(initialUserId = "luca")
        accountsService = FakeAccountsService()
        viewModel = AddContactViewModel(
            authService = authService,
            accountsService = accountsService,
            iODispatcher = dispatcher
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addContactViewModel_SearchExistingUser_StateUpdated() = runBlocking {
        assertEquals(0, viewModel.uiState.queryResults.size)
        viewModel.onEvent(AddContactEvent.QueueSearch("you"))
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.uiState.queryResults.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addContactViewModel_SearchNotExistingUser_StateUpdated() = runBlocking {
        assertEquals(0, viewModel.uiState.queryResults.size)
        viewModel.onEvent(AddContactEvent.QueueSearch("kartoffel"))
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, viewModel.uiState.queryResults.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addContactViewModel_RequestFriendship_RequestSent() = runBlocking {
        val initialYouFriends = accountsService.getFriendshipRequests("you").first().size
        viewModel.onEvent(AddContactEvent.QueueSearch("you"))
        dispatcher.scheduler.advanceUntilIdle()
        // I will ask friendship to the returned user
        assertEquals(1, viewModel.uiState.queryResults.size)
        viewModel.onEvent(AddContactEvent.SendFriendshipRequest(
            to = viewModel.uiState.queryResults[0]
        ))
        val updatedYouFriends = accountsService.getFriendshipRequests("you").first().size
        assertEquals(initialYouFriends+1, updatedYouFriends)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addContactViewModel_RequestFriendship_UiIsNotified() = runBlocking {
        val channel = viewModel.addContactUiEvent
        viewModel.onEvent(AddContactEvent.QueueSearch("you"))
        dispatcher.scheduler.advanceUntilIdle()
        // I will ask friendship to the returned user
        assertEquals(1, viewModel.uiState.queryResults.size)
        viewModel.onEvent(AddContactEvent.SendFriendshipRequest(
            to = viewModel.uiState.queryResults[0]
        ))
        // Correct message is shown
        assertEquals(
            R.string.friendship_request_correctly_sent,
            ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addContactViewModel_RequestDuplicateFriendship_UiIsNotified() = runBlocking {
        val channel = viewModel.addContactUiEvent
        viewModel.onEvent(AddContactEvent.QueueSearch("you"))
        dispatcher.scheduler.advanceUntilIdle()
        // I will ask friendship to the returned user
        assertEquals(1, viewModel.uiState.queryResults.size)
        viewModel.onEvent(AddContactEvent.SendFriendshipRequest(
            to = viewModel.uiState.queryResults[0]
        ))
        // Consume first ui snackbar event
        channel.first()
        viewModel.onEvent(AddContactEvent.SendFriendshipRequest(
            to = viewModel.uiState.queryResults[0]
        ))
        // Consume last ui snackbar event
        assertEquals(
            R.string.friendship_request_already_sent,
            ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
        )
    }
}
