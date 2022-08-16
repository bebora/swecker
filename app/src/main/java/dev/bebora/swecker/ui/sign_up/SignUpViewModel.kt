package dev.bebora.swecker.ui.sign_up

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.SETTINGS
import dev.bebora.swecker.SIGNUP
import dev.bebora.swecker.common.isValidEmail
import dev.bebora.swecker.common.isValidPassword
import dev.bebora.swecker.data.service.AccountService
import dev.bebora.swecker.ui.utils.onError
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService,
) : ViewModel() {
    var uiState by mutableStateOf(SignUpUiState())
        private set

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.SetTempEmail -> {
                uiState = uiState.copy(email = event.email)
            }
            is SignUpEvent.SetTempPassword -> {
                uiState = uiState.copy(password = event.password)
            }
            // TODO Use snackbar
            is SignUpEvent.SignInClick -> {
                if (!uiState.email.isValidEmail()) {
                    // SnackbarManager.showMessage(AppText.email_error)
                    uiState = uiState.copy(
                        isValidEmail = false
                    )
                    return
                }

                if (!uiState.password.isValidPassword()) {
                    // SnackbarManager.showMessage(AppText.password_error)
                    uiState = uiState.copy(
                        isValidPassword = false
                    )
                    return
                }

                viewModelScope.launch {
                    accountService.createAccount(uiState.email, uiState.password) { error ->
                        if (error == null) {
                            event.onNavigate()
                        } else {
                            onError(error = error)
                            uiState = uiState.copy(
                                errorMessage = error.localizedMessage ?: error.toString()
                            )
                        }
                    }
                }

            }
        }
    }
}
