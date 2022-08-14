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

    private val email get() = uiState.email
    private val password get() = uiState.password

    fun onEmailChange(newValue: String) {
        uiState = uiState.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState = uiState.copy(password = newValue)
    }

    fun onRepeatPasswordChange(newValue: String) {
        uiState = uiState.copy(repeatPassword = newValue)
    }

    // TODO Use snackbar
    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        if (!email.isValidEmail()) {
            // SnackbarManager.showMessage(AppText.email_error)
            uiState = uiState.copy(
                isValidEmail = false
            )
            return
        }

        if (!password.isValidPassword()) {
            // SnackbarManager.showMessage(AppText.password_error)
            uiState = uiState.copy(
                isValidPassword = false
            )
            return
        }

        if (password != uiState.repeatPassword) {
            // SnackbarManager.showMessage(AppText.password_match_error)
            uiState = uiState.copy(
                isValidRepeat = false
            )
            return
        }

        viewModelScope.launch {
            val oldUserId = accountService.getUserId()
            accountService.createAccount(email, password) { error ->
                if (error == null) {
                    linkWithEmail()
                    updateUserId(oldUserId, openAndPopUp)
                } else onError(error)
            }
        }
    }

    private fun linkWithEmail() {
        viewModelScope.launch {
            accountService.linkAccount(email, password) { error ->
                if (error != null) onError(error = error)
            }
        }
    }

    private fun updateUserId(oldUserId: String, openAndPopUp: (String, String) -> Unit) {
        viewModelScope.launch {
            val newUserId = accountService.getUserId()
            openAndPopUp(SETTINGS, SIGNUP)
            /* TODO add storageService
            storageService.updateUserId(oldUserId, newUserId) { error ->
                if (error != null) logService.logNonFatalCrash(error)
                else openAndPopUp(SETTINGS_SCREEN, SIGN_UP_SCREEN)
            }
             */
        }
    }
}
