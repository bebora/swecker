package dev.bebora.swecker.ui.sign_up

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false
)
