package dev.bebora.swecker.data.service.impl

import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.bebora.swecker.data.service.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthServiceImpl @Inject constructor() : AuthService {
    /*override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override fun isAnonymousUser(): Boolean {
        return Firebase.auth.currentUser?.isAnonymous ?: true
    }*/

    override fun getUserId(): String {
        return Firebase.auth.currentUser?.uid.orEmpty()
    }

    override fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                onResult(
                    when (it.exception) {
                        is FirebaseAuthInvalidUserException -> AuthInvalidUserException()
                        is FirebaseAuthInvalidCredentialsException -> AuthInvalidCredentialsException()
                        null -> null
                        else -> Exception()
                    }
                )
                // onResult(it.exception)
            }
    }

    override fun createAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                onResult(
                    when (it.exception) {
                        is FirebaseAuthWeakPasswordException -> AuthWeakPasswordException()
                        is FirebaseAuthInvalidCredentialsException -> AuthInvalidCredentialsException()
                        is FirebaseAuthUserCollisionException -> AuthUserCollisionException()
                        null -> null
                        else -> Exception()
                    }
                )
                // onResult(it.exception)
            }
    }

    /* override fun sendRecoveryEmail(email: String, onResult: (Throwable?) -> Unit) {
         Firebase.auth.sendPasswordResetEmail(email)
             .addOnCompleteListener { onResult(it.exception) }
     }

     override fun createAnonymousAccount(onResult: (Throwable?) -> Unit) {
         Firebase.auth.signInAnonymously()
             .addOnCompleteListener { onResult(it.exception) }
     }

     override fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
         val credential = EmailAuthProvider.getCredential(email, password)

         Firebase.auth.currentUser!!.linkWithCredential(credential)
             .addOnCompleteListener { onResult(it.exception) }
     }

     override fun deleteAccount(onResult: (Throwable?) -> Unit) {
         Firebase.auth.currentUser!!.delete()
             .addOnCompleteListener { onResult(it.exception) }
     }*/

    override fun logOut() {
        Firebase.auth.signOut()
    }

    override fun getUserInfoChanges(): Flow<Int> {
        var counter = 0
        return callbackFlow {
            val idTokenListener = FirebaseAuth.IdTokenListener {
                counter += 1
                trySend(counter)
            }
            Firebase.auth.addIdTokenListener(idTokenListener)
            awaitClose {
                Firebase.auth.removeIdTokenListener(idTokenListener)
            }
        }
    }
}
