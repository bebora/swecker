package dev.bebora.swecker.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.common.isValidEmail
import dev.bebora.swecker.common.isValidPassword
import dev.bebora.swecker.data.service.AuthInvalidCredentialsException
import dev.bebora.swecker.data.service.AuthInvalidUserException
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.ui.utils.UiText
import dev.bebora.swecker.ui.utils.onError
import dev.bebora.swecker.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.SetTempEmail -> {
                uiState = uiState.copy(email = event.email.trim())
            }
            is LoginEvent.SetTempPassword -> {
                uiState = uiState.copy(password = event.password.trim())
            }
            is LoginEvent.SignInClick -> {
                if (!uiState.email.isValidEmail()) {
                    viewModelScope.launch {
                        _uiEvent.send(
                            UiEvent.ShowSnackbar(
                                uiText = UiText.StringResource(
                                    resId = R.string.invalid_email
                                )
                            )
                        )
                    }
                    return
                }

                if (!uiState.password.isValidPassword()) {
                    viewModelScope.launch {
                        _uiEvent.send(
                            UiEvent.ShowSnackbar(
                                uiText = UiText.StringResource(
                                    resId = R.string.invalid_password
                                )
                            )
                        )
                    }
                    return
                }
                uiState = uiState.copy(
                    loading = true
                )
                // The viewModelScope is not actually needed as the onComplete function is just a callback
                //viewModelScope.launch {
                    authService.authenticate(uiState.email, uiState.password) { error ->
                        uiState = uiState.copy(
                            loading = false
                        )
                        if (error == null) {
                            event.onNavigate()
                        } else {
                            onError(error = error)
                            val stringRes = when (error) {
                                is AuthInvalidUserException -> R.string.invalid_user
                                is AuthInvalidCredentialsException -> R.string.wrong_password
                                else -> R.string.unknown_error
                            }
                            viewModelScope.launch {
                                _uiEvent.send(
                                    UiEvent.ShowSnackbar(
                                        uiText = UiText.StringResource(resId = stringRes)
                                    )
                                )
                            }
                        }
                    }
                //}
            }
        }
    }
}
