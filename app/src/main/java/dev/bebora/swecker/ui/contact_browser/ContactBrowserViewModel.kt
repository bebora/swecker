package dev.bebora.swecker.ui.contact_browser

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.service.ImageStorageService
import dev.bebora.swecker.data.service.AccountsService
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactBrowserViewModel @Inject constructor(
    private val authService: AuthService,
    private val accountsService: AccountsService,
) : ViewModel() {
    var uiState by mutableStateOf(ContactsUiState())
        private set

    var friends =
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
    }
}
