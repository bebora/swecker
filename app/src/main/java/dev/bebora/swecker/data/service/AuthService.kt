package dev.bebora.swecker.data.service

import kotlinx.coroutines.flow.Flow

interface AuthService {
    //fun hasUser(): Boolean
    //fun isAnonymousUser(): Boolean
    fun getUserId(): String
    fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun createAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    //fun sendRecoveryEmail(email: String, onResult: (Throwable?) -> Unit)
    //fun createAnonymousAccount(onResult: (Throwable?) -> Unit)
    //fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    //fun deleteAccount(onResult: (Throwable?) -> Unit)
    fun logOut()
    fun getUserInfoChanges(): Flow<Int>
}

class AuthInvalidUserException : Exception()
class AuthInvalidCredentialsException : Exception()
class AuthUserCollisionException : Exception()
class AuthWeakPasswordException : Exception()
