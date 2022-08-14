package dev.bebora.swecker.ui.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.service.AccountService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.SetTempEmail -> {
                uiState = uiState.copy(email = event.email)
            }
            is LoginEvent.SetTempPassword -> {
                uiState = uiState.copy(password = event.password)
            }
            is LoginEvent.SignInClick -> {
                if (uiState.email != "" && uiState.password != "") {
                    accountService.authenticate(uiState.email, uiState.password) { error ->
                        if (error == null) {
                            linkWithEmail()
                            uiState = uiState.copy(loggedIn = true)
                        } else onError(error = error)
                    }
                }
            }
        }
    }

    private val showErrorExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    private fun onError(error: Throwable) {
        Log.e("SWECKER-ERR", error.localizedMessage?:error.toString())
        uiState = uiState.copy(errorMessage = error.localizedMessage?:error.toString())
    }

    private fun linkWithEmail() {
        viewModelScope.launch(showErrorExceptionHandler) {
            accountService.linkAccount(uiState.email, uiState.password) { error ->
                if (error != null) onError(error)
            }
        }
    }
}
