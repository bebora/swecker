package dev.bebora.swecker.ui.add_group

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.ThinGroup
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.AccountsService
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.service.ImageStorageService
import dev.bebora.swecker.ui.utils.onError
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGroupViewModel @Inject constructor(
    private val authService: AuthService,
    private val accountsService: AccountsService,
    private val imageStorageService: ImageStorageService,
    private val alarmProviderService: AlarmProviderService,
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
                waitingForServiceResponse = true
            )
            alarmProviderService.createGroup(
                ownerId = uiState.me.id,
                userIds = listOf(uiState.me.id) + uiState.selectedMembers.map { it.id },
                onSuccess = {
                    uiState = uiState.copy(
                        tempGroupData = it,
                        waitingForServiceResponse = false,
                        content = AddGroupContent.GROUP_SELECT_NAME
                    )
                },
                onFailure = {
                    uiState = uiState.copy(
                        waitingForServiceResponse = false
                    )
                }

            )
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

    fun setGroupPic(groupPicUrl: Uri) {
        uiState = uiState.copy(
            waitingForServiceResponse = true
        )
        imageStorageService.setGroupPicture(
            groupId = uiState.tempGroupData.id,
            imageUri = groupPicUrl,
            onSuccess = { pictureUrl ->
                val newGroupData = uiState.tempGroupData.copy(
                    picture = pictureUrl
                )
                updateGroup(
                    newGroupData = newGroupData,
                    onComplete = {
                        uiState = uiState.copy(
                            waitingForServiceResponse = false
                        )
                        if (it != null) {
                            Log.d("SWECKER-UPD-GRP", "Error updating group with new picture")
                        } else {
                            uiState = uiState.copy(
                                tempGroupData = newGroupData
                            )
                        }
                    }
                )
            },
            onFailure = {
                Log.d("SWECKER-SET-PROPIC", it)
                uiState = uiState.copy(
                    waitingForServiceResponse = false
                )
            }
        )
    }

    fun confirmGroupCreation(onSuccess: () -> Unit) {
        uiState = uiState.copy(
            waitingForServiceResponse = true
        )
        updateGroup(
            newGroupData = uiState.tempGroupData.copy(
                name = uiState.groupName
            ),
            onComplete = {
                uiState = uiState.copy(
                    waitingForServiceResponse = false
                )
                if (it != null) {
                    Log.d("SWECKER-UPD-GRP-N", "Error updating group with new name")
                } else {
                    uiState = AddGroupUIState()
                    onSuccess()
                }
            }
        )
    }

    private fun updateGroup(newGroupData: ThinGroup, onComplete: (Throwable?) -> Unit) {
        alarmProviderService.updateGroup(
            newGroupData = newGroupData,
            onComplete = onComplete
        )
    }

    fun discardGroupCreation(onSuccess: () -> Unit) {
        onSuccess() // Allow the popup to be closed instantly
        if (uiState.tempGroupData.id.isNotBlank()) {
            alarmProviderService.deleteGroup(
                uiState.tempGroupData.id,
                onComplete = { groupDeletionError ->
                    if (groupDeletionError != null) {
                        Log.d("SWECKER-DELG-ERR", "Can't delete group", groupDeletionError)
                    }

                    imageStorageService.deleteGroupPicture(
                        groupId = uiState.tempGroupData.id,
                        onComplete = {
                            if (it != null) {
                                Log.d("SWECKER-DELGI-ERR", "Can't delete group image", it)
                            }
                        }
                    )
                }
            )
        }
    }
}

data class AddGroupUIState(
    val allContacts: List<User> = emptyList(),
    val selectedMembers: List<User> = emptyList(),
    val groupName: String = "",
    val content: AddGroupContent = AddGroupContent.GROUP_SELECT_CONTACTS,
    val me: User = User(),
    val waitingForServiceResponse: Boolean = false,
    val accountStatusLoaded: Boolean = false,
    val tempGroupData: ThinGroup = ThinGroup()
)

enum class AddGroupContent {
    GROUP_SELECT_NAME,
    GROUP_SELECT_CONTACTS
}
