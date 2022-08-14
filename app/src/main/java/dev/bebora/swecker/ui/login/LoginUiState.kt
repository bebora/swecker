package dev.bebora.swecker.ui.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loggedIn: Boolean = false,
    val errorMessage: String = ""
)
