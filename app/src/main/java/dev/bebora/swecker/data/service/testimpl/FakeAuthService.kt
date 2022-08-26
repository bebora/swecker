package dev.bebora.swecker.data.service.testimpl

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dev.bebora.swecker.data.service.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAuthService : AuthService {
    private var userId: String? = null
    private var infoChanges: Int = 0

    override fun getUserId(): String {
        return userId ?: ""
    }

    override fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit) {
        if (email != validLoginEmail) {
            onResult(FirebaseAuthInvalidUserException("code", "message"))
        }
        else if (password != validPassword ) {
            onResult(FirebaseAuthInvalidCredentialsException("code", "message"))
        }
        else {
            userId = validUserId
            infoChanges += 1
        }
    }

    override fun createAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        if (email == validLoginEmail) {
            onResult(FirebaseAuthUserCollisionException("code", "message"))
        }
        else if (password == invalidSignupPassword ) {
            onResult(FirebaseAuthWeakPasswordException("code", "message", "no"))
        }
        else if (email == invalidSignupEmail) {
            onResult(FirebaseAuthInvalidCredentialsException("code", "message"))
        }
        else {
            userId = validUserId
            infoChanges += 1
        }
    }

    override fun logOut() {
        userId = null
        infoChanges += 1
    }

    override fun getUserInfoChanges(): Flow<Int> {
        return flow {
            emit(infoChanges)
        }
    }

    companion object {
        const val validPassword = "dolphins"
        const val validUserId = "testuserid"
        const val validLoginEmail = "a@b.org"
        const val invalidSignupEmail = "hello"
        const val invalidSignupPassword = "weak"
    }
}
