package dev.bebora.swecker.data.service.impl

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.StorageService

class StorageServiceImpl : StorageService {
    override fun getUser(
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (User) -> Unit
    ) {
        if (userId == "") {
            Log.d("SWECKER-DEB", "userId is empty")
        }
        else {
            Firebase.firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnFailureListener { error -> onError(error) }
                .addOnSuccessListener { result ->
                    val user = result.toObject<User>()?.copy(id = result.id)
                    onSuccess(user ?: User())
                }
        }
    }

    override fun addUserListener() {
        TODO("Not yet implemented")
    }

    override fun saveUser(user: User, onResult: (Throwable?) -> Unit) {
        Log.d("SWECKER-SAVE", "Sto salvando user così $user")
        Firebase.firestore
            .collection(USERS_COLLECTION)
            .document(user.id)
            .set(user)
            .addOnCompleteListener { onResult(it.exception) }
    }

    /*
    override fun updateUser(user: User, onResult: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(USERS_COLLECTION)
            .document(user.id)
    }
     */

    companion object {
        private val USERS_COLLECTION = "users"
    }
}
