package dev.bebora.swecker.ui.add_group

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.AccountsService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.ui.utils.onError
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGroupViewModel @Inject constructor(
    private val authService: AuthService,
    private val accountsService: AccountsService,
) : ViewModel() {
    private val userInfoChanges = authService.getUserInfoChanges()

    var uiState by mutableStateOf(AddGroupUIState())
        private set

    private var friends =
        accountsService.getFriends(authService.getUserId())
    
    init {
        viewModelScope.launch {
            friends.collect {
                uiState = uiState.copy(
                    allContacts = it
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
                            me = it,
                            accountStatusLoaded = true
                        )
                    },
                    onError = {
                        uiState = uiState.copy(
                            accountStatusLoaded = true
                        )
                        onError(it)
                    }
                )
            }
        }
    }


    fun toggleContactSelection(user: User) {
        if (uiState.selectedMembers.contains(user)) {
            uiState = uiState.copy(
                selectedMembers = uiState.selectedMembers.minus(user)
            )
        } else {
            uiState = uiState.copy(
                selectedMembers = uiState.selectedMembers.plus(user)
            )
        }
    }

    fun nextScreen() {
        if (uiState.content == AddGroupContent.GROUP_SELECT_CONTACTS) {
            uiState = uiState.copy(
                content = AddGroupContent.GROUP_SELECT_NAME
            )
        }
    }

    fun previousScreen() {
        if (uiState.content == AddGroupContent.GROUP_SELECT_NAME) {
            uiState = uiState.copy(
                content = AddGroupContent.GROUP_SELECT_CONTACTS
            )
        }
    }

    fun setGroupName(groupName: String) {
        uiState = uiState.copy(
            groupName = groupName
        )
    }

    fun setGroupPic(groupPicUrl: String) {
        uiState = uiState.copy(
            groupPicUrl = groupPicUrl
        )
    }

    fun createGroup() {
        //TODO add actual group creation
        uiState = AddGroupUIState()
    }
}

data class AddGroupUIState(
    val allContacts: List<User> = emptyList(),
    val selectedMembers: List<User> = emptyList(),
    val groupPicUrl: String = "",
    val groupName: String = "",
    val content: AddGroupContent = AddGroupContent.GROUP_SELECT_CONTACTS,
    val me: User = User(),
    val accountStatusLoaded: Boolean = false
)

enum class AddGroupContent {
    GROUP_SELECT_NAME,
    GROUP_SELECT_CONTACTS
}
