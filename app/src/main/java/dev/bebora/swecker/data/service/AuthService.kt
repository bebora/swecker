package dev.bebora.swecker.data.service

import kotlinx.coroutines.flow.Flow

interface AuthService {
    fun getUserId(): String
    fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun createAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun logOut()
    fun getUserInfoChanges(): Flow<Int>
}

class AuthInvalidUserException : Exception()
class AuthInvalidCredentialsException : Exception()
class AuthUserCollisionException : Exception()
class AuthWeakPasswordException : Exception()
