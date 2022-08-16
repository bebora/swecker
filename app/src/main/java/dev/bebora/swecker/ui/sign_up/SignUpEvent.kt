package dev.bebora.swecker.ui.sign_up

sealed class SignUpEvent {
    data class SetTempEmail(val email: String) : SignUpEvent()
    data class SetTempPassword(val password: String) : SignUpEvent()
    data class SignInClick(val onNavigate: () -> Unit) : SignUpEvent()
}
