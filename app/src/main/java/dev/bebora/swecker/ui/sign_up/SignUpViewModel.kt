package dev.bebora.swecker.ui.sign_up

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.common.isValidEmail
import dev.bebora.swecker.common.isValidPassword
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.*
import dev.bebora.swecker.ui.utils.UiText
import dev.bebora.swecker.ui.utils.onError
import dev.bebora.swecker.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authService: AuthService,
    private val accountsService: AccountsService,
) : ViewModel() {
    var uiState by mutableStateOf(SignUpUiState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.SetTempEmail -> {
                uiState = uiState.copy(email = event.email.trim())
            }
            is SignUpEvent.SetTempPassword -> {
                uiState = uiState.copy(password = event.password.trim())
            }
            is SignUpEvent.SignUpClick -> {
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
                    authService.createAccount(uiState.email, uiState.password) { error ->
                        if (error == null) {
                            accountsService.saveUser(
                                requestedUser = User(
                                    id = authService.getUserId(),
                                    name = uiState.email.split("@")[0],
                                    username = authService.getUserId(),
                                    propicUrl = ""
                                ),
                                oldUser = null,
                            ) { saveUserError ->
                                uiState = uiState.copy(
                                    loading = false
                                )
                                if (saveUserError == null) {
                                    // Technically useless, but will trigger the id token listeners
                                    authService.authenticate(
                                        email = uiState.email,
                                        password = uiState.password
                                    ) {
                                        if (it != null) {
                                            onError(it)
                                        }
                                        else {
                                            event.onNavigate()
                                        }
                                    }
                                }
                                else {
                                    onError(error = saveUserError)
                                    viewModelScope.launch {
                                        _uiEvent.send(
                                            UiEvent.ShowSnackbar(
                                                uiText = UiText.StringResource(resId = R.string.signup_error)
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            uiState = uiState.copy(
                                loading = false
                            )
                            onError(error = error)
                            val stringRes = when (error) {
                                is AuthWeakPasswordException -> R.string.invalid_password
                                is AuthInvalidCredentialsException -> R.string.invalid_email
                                is AuthUserCollisionException -> R.string.email_already_exists
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
