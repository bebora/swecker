package dev.bebora.swecker.ui.sign_up

//TODO isValidField serves no purpose at the moment, errors should be shown to the user
data class SignUpUiState(
    val email: String = "",
    val password: String = ""
)
