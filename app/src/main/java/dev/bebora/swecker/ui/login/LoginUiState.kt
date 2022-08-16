package dev.bebora.swecker.ui.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String = "",
    val isValidEmail: Boolean = true,
    val isValidPassword: Boolean = true
)
