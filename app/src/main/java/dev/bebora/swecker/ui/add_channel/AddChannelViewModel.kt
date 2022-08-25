package dev.bebora.swecker.ui.add_channel

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
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class AddChannelViewModel @Inject constructor(
    private val authService: AuthService,
    private val accountsService: AccountsService,
    private val imageStorageService: ImageStorageService,
    private val alarmProviderService: AlarmProviderService,
) : ViewModel() {
    private val userInfoChanges = authService.getUserInfoChanges()

    var uiState by mutableStateOf(AddChannelUIState())
        private set

    init {
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

    fun setChannelName(channelName: String) {
        uiState = uiState.copy(
            channelName = channelName
        )
    }

    fun setChannelHandle(channelHandle: String) {
        uiState = uiState.copy(
            channelHandle = channelHandle
        )
    }

    fun setChannelPic(channelPicUrl: Uri) {
        uiState = uiState.copy(
            waitingForServiceResponse = true
        )

        imageStorageService.setChannelPicture(
            channelId = uiState.channelId,
            imageUri = channelPicUrl,
            onSuccess = { pictureUrl ->
                uiState = uiState.copy(
                    waitingForServiceResponse = false,
                    uploadedPicUrl = pictureUrl
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

    fun confirmChannelCreation(onSuccess: () -> Unit) {
        uiState = uiState.copy(
            waitingForServiceResponse = true
        )
        alarmProviderService.createChannel(
            channel = ThinGroup(
                id = uiState.channelId,
                name = uiState.channelName,
                members = listOf(uiState.me.id),
                owner = uiState.me.id,
                handle = uiState.channelHandle,
                picture = uiState.uploadedPicUrl
            ),
            onComplete = {
                uiState = uiState.copy(
                    waitingForServiceResponse = false
                )
                if (it != null) {
                    Log.d("SWECKER-UPD-GRP-N", "Error creating channel $it")
                } else {
                    onSuccess()
                    uiState = AddChannelUIState()

                }
            }
        )
    }

    fun discardChannelCreation(onSuccess: () -> Unit) {
        onSuccess() // Allow the popup to be closed instantly


        imageStorageService.deleteChannelPicture(
            channelId = uiState.channelId,
            onComplete = {
                if (it != null) {
                    Log.d("SWECKER-DELGI-ERR", "Can't delete channel image", it)
                }
            }
        )
        
        uiState = AddChannelUIState()
    }
}

data class AddChannelUIState(
    val channelName: String = "",
    val channelHandle: String = "",
    val channelId: String = UUID.randomUUID().toString(),
    val channelPicUrl: Uri = Uri.EMPTY,
    val uploadedPicUrl: String = "",
    val me: User = User(),
    val waitingForServiceResponse: Boolean = false,
    val accountStatusLoaded: Boolean = false,
)

