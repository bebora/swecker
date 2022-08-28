package dev.bebora.swecker.data.service.testimpl

import dev.bebora.swecker.data.service.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthService(val initialUserId: String? = null) : AuthService {
    private var userId: String? = initialUserId
    private var acceptSignUpCredentials: Boolean = false
    private val settingsFlow = MutableStateFlow(0)

    override fun getUserId(): String {
        return userId ?: ""
    }

    override fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit) {
        val bypassChecks = email == validSignupEmail && acceptSignUpCredentials
        if (email != validLoginEmail && !bypassChecks) {
            onResult(AuthInvalidUserException())
        }
        else if (password != validPassword && !bypassChecks) {
            onResult(AuthInvalidCredentialsException())
        }
        else {
            userId = validUserId
            settingsFlow.value += 1
            onResult(null)
        }
    }

    override fun createAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        if (email == validLoginEmail) {
            onResult(AuthUserCollisionException())
        }
        else if (password == invalidSignupPassword ) {
            onResult(AuthWeakPasswordException())
        }
        else if (email == invalidSignupEmail) {
            onResult(AuthInvalidCredentialsException())
        }
        else {
            acceptSignUpCredentials = true
            userId = validUserId
            settingsFlow.value += 1
            onResult(null)
        }
    }

    override fun logOut() {
        userId = null
        settingsFlow.value += 1
    }

    override fun getUserInfoChanges(): Flow<Int> {
        return settingsFlow.asStateFlow()
    }

    companion object {
        const val validPassword = "D0lphins"
        const val validUserId = "testuserid"
        const val validLoginEmail = "a@b.org"
        const val invalidSignupEmail = "hello"
        const val invalidSignupPassword = "weak"
        const val disabledUserEmail = "lo@ack.er"
        const val wrongPassword = "Che3bonta"
        const val validSignupEmail = "we@are.us"
    }
}
