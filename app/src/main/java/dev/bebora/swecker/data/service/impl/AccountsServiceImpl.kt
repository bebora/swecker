package dev.bebora.swecker.data.service.impl

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.*
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
            Log.d("SWECKER-DEB", "userId is $userId")
            Firebase.firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnFailureListener { error -> onError(error) }
                .addOnSuccessListener { result ->
                    Log.d("SWECKER-RESULT-GET", "Result is $result")
                    val user = result.toObject<User>()
                    onSuccess(user ?: User())
                }
        }
    }

    override fun addUserListener() {
        TODO("Not yet implemented")
    }

    override fun saveUser(requestedUser: User, update: Boolean, onResult: (Throwable?) -> Unit) {
        var user = requestedUser.copy(
            username = requestedUser.username.lowercase()
        )
        Log.d("SWECKER-SAVE", "Sto salvando user così $user")
        // An error on signup may cause empty user data
        if (user.name.isBlank() && user.username.isBlank()) {
            user = user.copy(name = user.id, username = user.id.lowercase())
        } else if (user.name.isBlank() || user.username.isBlank()) {
            onResult(BlankUserOrUsernameException())
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
                            "username", user.username,
                            "propicUrl", user.propicUrl
                        ).addOnCompleteListener {
                            //onResult(it.exception)
                            updateFriendshipRequests(user = user, onResult = onResult)
                        }
                    } else {
                        docRef
                            .set(user)
                            .addOnCompleteListener { onResult(it.exception) }
                    }
                } else {
                    onResult(UsernameAlreadyTakenException())
                }
            }
    }

    override fun getFriends(userId: String): Flow<List<User>> {
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
        if (from.id == to.id) {
            onResult(FriendshipRequestToYourselfException())
            return
        }
        val collectionRef = Firebase.firestore
            .collection(FRIENDSHIP_REQUESTS_COLLECTION)
        collectionRef
            .whereEqualTo("from.id", from.id)
            .whereEqualTo("to.id", to.id)
            .get()
            .addOnFailureListener { error -> onResult(error) }
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    collectionRef.add(FriendshipRequest(from = from, to = to))
                        .addOnFailureListener(onResult)
                        .addOnSuccessListener {
                            onResult(null)
                        }
                } else {
                    onResult(FriendshipRequestAlreadySentException())
                }
            }
    }

    override fun acceptFriendship(me: User, newFriend: User, onResult: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getFriendshipRequests(userId: String): Flow<List<User>> {
        return callbackFlow {
            val listener = Firebase.firestore
                .collection(FRIENDSHIP_REQUESTS_COLLECTION)
                .whereEqualTo("to.id", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("SWECKER-GET-REQUESTS", "Listen failed.", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        trySend(
                            snapshot
                                .toObjects(FriendshipRequest::class.java)
                                .map { req -> req.from }
                        )
                    } else {
                        Log.d("SWECKER-FRIENDSREQ-NOPE", "Current data: null")
                        trySend(emptyList())
                    }
                }
            awaitClose {
                listener.remove()
            }
        }
    }

    override fun searchUsers(
        from: User,
        query: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<User>) -> Unit
    ) {
        Log.d("SWECKER-SEARCH", "Searching for users starting with '$query'")
        if (query.isEmpty()) {
            onSuccess(emptyList())
        } else {
            Firebase.firestore
                .collection(USERS_COLLECTION)
                .whereGreaterThanOrEqualTo("username", query)
                .whereLessThanOrEqualTo(
                    "username",
                    "${query}~"
                ) // https://stackoverflow.com/questions/46568142/google-firestore-query-on-substring-of-a-property-value-text-search
                .whereNotEqualTo("username", from.username)
                .get()
                .addOnFailureListener(onError)
                .addOnSuccessListener { querySnapshot ->
                    onSuccess(
                        querySnapshot.toObjects(User::class.java)
                    )
                }
        }
    }

    private fun updateFriendshipRequests(user: User, onResult: (Throwable?) -> Unit) {
        Log.d("SWECKER-UPD-FRIENDREQ", "Updating friendships requests with new user data")
        val batch = Firebase.firestore.batch()
        val friendReqCollection = Firebase.firestore
            .collection(FRIENDSHIP_REQUESTS_COLLECTION)
        friendReqCollection
            .whereEqualTo("to.id", user.id)
            .get()
            .addOnFailureListener { error -> onResult(error) }
            .addOnSuccessListener { querySnapshotTo ->
                querySnapshotTo.forEach { res ->
                    val docRef = friendReqCollection.document(res.id)
                    batch.update(docRef, "to", user)
                }
                friendReqCollection
                    .whereEqualTo("from.id", user.id)
                    .get()
                    .addOnFailureListener { error -> onResult(error) }
                    .addOnSuccessListener { querySnapshotFrom ->
                        querySnapshotFrom.forEach { res ->
                            val docRef = friendReqCollection.document(res.id)
                            batch.update(docRef, "from", user)
                        }
                        batch.commit()
                            .addOnFailureListener { onResult(it) }
                            .addOnSuccessListener { onResult(null) } // No error
                    }
            }
    }

    companion object {
        private val USERS_COLLECTION = "users"
        private val FRIENDSHIP_REQUESTS_COLLECTION = "friendrequests"
    }
}

data class UserWithFriends(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val propicUrl: String = "",
    val friends: List<User> = emptyList()
)

data class FriendshipRequest(
    val from: User = User(),
    val to: User = User()
)
