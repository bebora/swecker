package dev.bebora.swecker.data.service.impl

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.BlankUserOrUsername
import dev.bebora.swecker.data.service.StorageService
import dev.bebora.swecker.data.service.UsernameAlreadyTakenException

class StorageServiceImpl : StorageService {
    override fun getUser(
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (User) -> Unit
    ) {
        if (userId == "") {
            Log.d("SWECKER-DEB", "userId is empty")
        } else {
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
        if (user.name.isBlank() || user.username.isBlank()) {
            onResult(BlankUserOrUsername())
        } else {
            Firebase.firestore
                .collection(USERS_COLLECTION)
                .whereEqualTo("username", user.username)
                .get()
                .addOnFailureListener { error -> onResult(error) }
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty || querySnapshot.documents[0].get("id") == user.id) {
                        Firebase.firestore
                            .collection(USERS_COLLECTION)
                            .document(user.id)
                            .set(user)
                            .addOnCompleteListener { onResult(it.exception) }
                    } else {
                        onResult(UsernameAlreadyTakenException())
                    }
                }
        }
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
