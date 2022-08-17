package dev.bebora.swecker.ui.sign_up

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.common.isValidEmail
import dev.bebora.swecker.common.isValidPassword
import dev.bebora.swecker.data.service.AccountService
import dev.bebora.swecker.ui.utils.UiText
import dev.bebora.swecker.ui.utils.onError
import dev.bebora.swecker.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService,
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
            is SignUpEvent.SignInClick -> {
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

                viewModelScope.launch {
                    accountService.createAccount(uiState.email, uiState.password) { error ->
                        if (error == null) {
                            event.onNavigate()
                        } else {
                            onError(error = error)
                            val stringRes = when (error) {
                                is FirebaseAuthWeakPasswordException -> R.string.invalid_password
                                is FirebaseAuthInvalidCredentialsException -> R.string.invalid_email
                                is FirebaseAuthUserCollisionException -> R.string.email_already_exists
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
                }
            }
        }
    }
}
