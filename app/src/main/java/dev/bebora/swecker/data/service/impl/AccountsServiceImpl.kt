package dev.bebora.swecker.data.service.impl

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.BlankUserOrUsername
import dev.bebora.swecker.data.service.AccountsService
import dev.bebora.swecker.data.service.UsernameAlreadyTakenException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AccountsServiceImpl : AccountsService {
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

    override fun saveUser(requestedUser: User, update: Boolean, onResult: (Throwable?) -> Unit) {
        var user = requestedUser.copy()
        Log.d("SWECKER-SAVE", "Sto salvando user così $user")
        // An error on signup may cause empty user data
        if (user.name.isBlank() && user.username.isBlank()) {
            user = user.copy(name = user.id, username = user.id)
        } else if (user.name.isBlank() || user.username.isBlank()) {
            onResult(BlankUserOrUsername())
            return
        }
        Firebase.firestore
            .collection(USERS_COLLECTION)
            .whereEqualTo("username", user.username)
            .get()
            .addOnFailureListener { error -> onResult(error) }
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty || querySnapshot.documents[0].get("id") == user.id) {
                    val docRef = Firebase.firestore
                        .collection(USERS_COLLECTION)
                        .document(user.id)

                    if (update) {
                        docRef.update(
                            "name", user.name,
                            "username", user.username
                        ).addOnCompleteListener { onResult(it.exception) }
                    } else {
                        docRef.set(user)
                    }
                } else {
                    onResult(UsernameAlreadyTakenException())
                }
            }
    }

    override fun getFriends(userId: String, onError: (Throwable?) -> Unit): Flow<List<User>> {
        return callbackFlow {
            val listener = Firebase.firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("SWECKER-GET-FRIENDS", "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d("SWECKER-GET-FRIENDS-EXISTS", "Current data: ${snapshot.data}")
                        trySend(
                            snapshot.toObject(UserWithFriends::class.java)?.friends ?: emptyList()
                        )
                    } else {
                        Log.d("SWECKER-GET-FRIENDS-NOPE", "Current data: null")
                        trySend(emptyList())
                    }

                }
            awaitClose {
                listener.remove()
            }
        }
    }

    override fun requestFriendship(from: User, to: User, onResult: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun acceptFriendship(me: User, newFriend: User, onResult: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getFriendshipRequests(user: User, onResult: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }


    companion object {
        private val USERS_COLLECTION = "users"
    }
}

data class UserWithFriends(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val propicUrl: String = "",
    val friends: List<User> = emptyList()
)
