package dev.bebora.swecker.ui.contact_browser.add_contact

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.AccountsService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.service.FriendshipRequestAlreadySentException
import dev.bebora.swecker.data.service.FriendshipRequestToYourselfException
import dev.bebora.swecker.ui.utils.UiText
import dev.bebora.swecker.ui.utils.onError
import dev.bebora.swecker.util.UiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val authService: AuthService,
    private val accountsService: AccountsService,
) : ViewModel() {
    private val userInfoChanges = authService.getUserInfoChanges()

    private var searchJob: Job? = null
    var uiState by mutableStateOf(AddContactUiState())
        private set

    private var friends =
        accountsService.getFriends(authService.getUserId())

    private val _addContactUiEvent = Channel<UiEvent>()
    val addContactUiEvent = _addContactUiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            userInfoChanges.collect {
                Log.d("SWECKER-CHANGE-AUTH", "Rilevato cambio utente")
                accountsService.getUser(
                    userId = authService.getUserId(),
                    onSuccess = {
                        uiState = uiState.copy(
                            me = it
                        )
                    },
                    onError = ::onError
                )
            }
        }
        viewModelScope.launch {
            friends.collect { updatedFriends ->
                uiState = uiState.copy(
                    friendsIds = updatedFriends.map { it.id }.toSet()
                )
            }
        }
    }

    private fun searchDebounced(searchText: String) {
        if (uiState.me.id.isBlank()) {
            Log.d("SWECKER-", "id vuoto")
            return
        }
        uiState = uiState.copy(
            processingQuery = true
        )
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(250)
            accountsService.searchUsers(
                from = uiState.me,
                query = searchText,
                onError = { Log.e("SWECKER-SEARCH-ERR", it.message ?: "Generic search error") },
                onSuccess = {
                    uiState = uiState.copy(
                        queryResults = it,
                        processingQuery = false
                    )
                }
            )
        }
    }

    fun onEvent(event: AddContactEvent) {
        when (event) {
            is AddContactEvent.QueueSearch -> {
                uiState = uiState.copy(
                    currentQuery = event.query
                )
                val actualquery = event.query.lowercase()
                searchDebounced(searchText = actualquery)
            }
            is AddContactEvent.SendFriendshipRequest -> {
                uiState = uiState.copy(
                    uploadingFriendshipRequest = true
                )
                accountsService.requestFriendship(
                    from = uiState.me,
                    to = event.to
                ) { error ->
                    uiState = uiState.copy(
                        uploadingFriendshipRequest = false
                    )
                    if (error == null) {
                        // TODO do something with successful request
                        viewModelScope.launch {
                            _addContactUiEvent.send(
                                UiEvent.ShowSnackbar(
                                    uiText = UiText.StringResource(resId = R.string.friendship_request_correctly_sent)
                                )
                            )
                        }
                    } else {
                        onError(error)
                        val stringRes = when (error) {
                            is FriendshipRequestAlreadySentException -> R.string.friendship_request_already_sent
                            is FriendshipRequestToYourselfException -> R.string.friendship_request_to_yourself
                            else -> R.string.request_friendship_error
                        }
                        viewModelScope.launch {
                            _addContactUiEvent.send(
                                UiEvent.ShowSnackbar(
                                    uiText = UiText.StringResource(resId = stringRes)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
