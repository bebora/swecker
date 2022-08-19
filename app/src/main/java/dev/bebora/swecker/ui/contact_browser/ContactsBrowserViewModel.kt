package dev.bebora.swecker.ui.contact_browser

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.*
import dev.bebora.swecker.ui.utils.UiText
import dev.bebora.swecker.ui.utils.onError
import dev.bebora.swecker.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsBrowserViewModel @Inject constructor(
    private val authService: AuthService,
    private val accountsService: AccountsService,
) : ViewModel() {
    private val userInfoChanges = authService.getUserInfoChanges()
    var uiState by mutableStateOf(ContactsUiState())
        private set

    private val _contactsUiEvent = Channel<UiEvent>()
    val contactsUiEvent = _contactsUiEvent.receiveAsFlow()

    private var friends =
        accountsService.getFriends(
            authService.getUserId(),
            onError = { Log.d("SWECKER-VIEWMODEL", it.toString()) })

    init {
        viewModelScope.launch {
            friends.collect {
                uiState = uiState.copy(
                    friends = it
                )
            }
        }
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
    }

    fun onEvent(event: ContactsEvent) {
        when (event) {
            is ContactsEvent.RequestFriendship -> {
                uiState = uiState.copy(
                    uploadingFriendshipRequest = true
                )
                accountsService.requestFriendship(
                    from = event.from,
                    to = event.to
                ) { error ->
                    uiState = uiState.copy(
                        uploadingFriendshipRequest = false
                    )
                    if (error == null) {
                        // TODO do something with successful request
                        viewModelScope.launch {
                            _contactsUiEvent.send(
                                UiEvent.ShowSnackbar(
                                    uiText = UiText.StringResource(resId = R.string.friendship_request_correctly_sent)
                                )
                            )
                        }
                    } else {
                        onError(error)
                        val stringRes = when (error) {
                            is FriendshipRequestAlreadySentException -> R.string.friendship_request_already_sent
                            else -> R.string.request_friendship_error
                        }
                        viewModelScope.launch {
                            _contactsUiEvent.send(
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
