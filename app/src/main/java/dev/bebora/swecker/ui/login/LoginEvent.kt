package dev.bebora.swecker.ui.login

sealed class LoginEvent {
    data class SetTempEmail(val email: String) : LoginEvent()
    data class SetTempPassword(val password: String) : LoginEvent()
    data class SignInClick(val onNavigate: () -> Unit) : LoginEvent()
}
