package dev.bebora.swecker.data.service.impl

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.*
import dev.bebora.swecker.data.service.impl.FirebaseConstants.FRIENDSHIP_REQUESTS_COLLECTION
import dev.bebora.swecker.data.service.impl.FirebaseConstants.USERS_COLLECTION
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class AccountsServiceImpl : AccountsService {
    override fun getUser(
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (User) -> Unit
    ) {
        if (userId.isBlank()) {
            Log.d("SWECKER-DEB", "userId is empty")
            onError(BlankUserOrUsernameException())
        } else {
            Log.d("SWECKER-DEB", "userId is $userId")
            Firebase.firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnFailureListener { error -> onError(error) }
                .addOnSuccessListener { result ->
                    Log.d("SWECKER-RESULT-GET", "Result is $result")
                    val user = result.toObject<UserWithFriends>()?.toUser()
                    onSuccess(user ?: User())
                }
        }
    }

    /*private fun getUserAsFlow(userId: String): Flow<User> {
        if (userId.isBlank()) {
            return emptyFlow()
        } else {
            return callbackFlow {
                val listener = Firebase.firestore
                    .collection(USERS_COLLECTION)
                    .document(userId)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w("SWECKER-GET-USER", "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            Log.d("SWECKER-GET-USER-EXISTS", "Current data: ${snapshot.data}")
                            trySend(
                                snapshot.toObject(UserWithFriends::class.java)?.toUser() ?: User()
                            )
                        } else {
                            Log.d("SWECKER-GET-USER-NOPE", "Current data: null")
                            trySend(User())
                        }
                    }
                awaitClose {
                    listener.remove()
                }
            }
        }
    }

    override fun getUsersAsFlow(): Flow<Map<String, User>> {
        val allUser = mutableMapOf<String, User>()
        return callbackFlow {
            val listener = Firebase.firestore
                .collection(USERS_COLLECTION)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w("SWECKER-GLOBAL-ERR", "listen:error", e)
                        return@addSnapshotListener
                    }
                    Log.d("SWECKER-GET-GLOBAL", "Something changed")
                    for (dc in snapshots!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val newUser = dc.document.toObject(UserWithFriends::class.java)
                                allUser[newUser.id] = newUser.toUser()
                            }
                            DocumentChange.Type.MODIFIED -> {
                                val newUser = dc.document.toObject(UserWithFriends::class.java)
                                allUser.replace(newUser.id, newUser.toUser())
                            }
                            DocumentChange.Type.REMOVED -> {
                                val newUser = dc.document.toObject(UserWithFriends::class.java)
                                allUser.remove(newUser.id)
                            }
                        }
                    }
                    trySend(
                        allUser
                    )
                }
            awaitClose {
                listener.remove()
            }
        }
    }*/

    override fun saveUser(requestedUser: User, oldUser: User?, onResult: (Throwable?) -> Unit) {
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

                    if (oldUser != null) {
                        docRef.update(
                            "name", user.name,
                            "username", user.username,
                            "propicUrl", user.propicUrl
                        ).addOnCompleteListener {
                            //onResult(it.exception)
                            updateFriendshipRequests(user = user) {
                                if (it == null) {
                                    updateOwnInfoInFriendsDocuments(
                                        newMe = requestedUser,
                                        oldMe = oldUser
                                    ) { updateInfoException ->
                                        onResult(updateInfoException)
                                    }
                                }
                            }
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
        if (userId.isBlank()) {
            Log.d("SWECKER-NOID", "Can't get friends without id")
            return MutableStateFlow(emptyList<User>()).asStateFlow()
        }
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
                        .addOnCompleteListener {
                            onResult(it.exception)
                        }
                } else {
                    onResult(FriendshipRequestAlreadySentException())
                }
            }
    }

    override fun acceptFriendship(me: User, newFriend: User, onResult: (Throwable?) -> Unit) {
        val friendRequestsRef = Firebase.firestore
            .collection(FRIENDSHIP_REQUESTS_COLLECTION)
        friendRequestsRef
            .whereEqualTo("from.id", newFriend.id)
            .whereEqualTo("to.id", me.id)
            .get()
            .addOnFailureListener(onResult)
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    onResult(FriendshipRequestNotExistingException())
                } else {
                    val batch = Firebase.firestore.batch()
                    val usersRef = Firebase.firestore
                        .collection(USERS_COLLECTION)
                    val meRef = usersRef
                        .document(me.id)
                    batch.update(meRef, "friends", FieldValue.arrayUnion(newFriend))
                    val newFriendRef = usersRef
                        .document(newFriend.id)
                    batch.update(newFriendRef, "friends", FieldValue.arrayUnion(me))
                    batch.commit()
                        .addOnFailureListener(onResult)
                        .addOnSuccessListener {
                            val toDeleteId = querySnapshot.documents[0].id
                            friendRequestsRef
                                .document(toDeleteId)
                                .delete()
                                .addOnFailureListener(onResult)
                                .addOnSuccessListener {
                                    onResult(null)
                                }
                        }
                }
            }
    }

    override fun getFriendshipRequests(userId: String): Flow<List<User>> {
        if (userId.isBlank()) {
            Log.d("SWECKER-NOID", "Can't get friend requests without id")
            return MutableStateFlow(emptyList<User>()).asStateFlow()
        }
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
                // .whereNotEqualTo("id", from.id) // https://firebase.google.com/docs/firestore/query-data/queries#compound_queries
                .get()
                .addOnFailureListener(onError)
                .addOnSuccessListener { querySnapshot ->
                    onSuccess(
                        querySnapshot.toObjects(UserWithFriends::class.java)
                            .filter { it.id != from.id }
                            .map { it.toUser() }
                    )
                }
        }
    }

    override fun removeFriend(me: User, friend: User, onResult: (Throwable?) -> Unit) {
        if (me.id.isBlank() || friend.id.isBlank()) {
            onResult(UserNotFoundException())
        } else {
            val store = Firebase.firestore
            val usersRef = store.collection(USERS_COLLECTION)
            val meRef = usersRef.document(me.id)
            val friendRef = usersRef.document(friend.id)
            store.runTransaction { transaction ->
                val meInDb = transaction.get(meRef)
                val friendInDb = transaction.get(friendRef)
                if (!meInDb.exists() || !friendInDb.exists()) {
                    throw UserNotFoundException()
                } else {
                    transaction.update(meRef, "friends", FieldValue.arrayRemove(friend))
                    transaction.update(friendRef, "friends", FieldValue.arrayRemove(me))
                    null
                }
            }
                .addOnCompleteListener {
                    onResult(it.exception)
                }
        }
    }

    /**
     * To be called after the user has changed name, username or profile picture
     */
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

    private fun updateOwnInfoInFriendsDocuments(
        newMe: User,
        oldMe: User,
        onResult: (Throwable?) -> Unit
    ) {
        Log.d("SWECKER-UPD-FRIDATA", "Updating friends with new user data")
        if (newMe.id.isBlank()) {
            onResult(UserNotFoundException())
        } else {
            val store = Firebase.firestore
            val usersRef = store.collection(USERS_COLLECTION)
            val meRef = usersRef.document(newMe.id)
            store.runTransaction { transaction ->
                val meInDb = transaction.get(meRef)
                val friends = meInDb.toObject(UserWithFriends::class.java)?.friends ?: emptyList()
                friends.forEach {
                    transaction.update(
                        usersRef.document(it.id),
                        "friends",
                        FieldValue.arrayRemove(oldMe)
                    )
                    transaction.update(
                        usersRef.document(it.id),
                        "friends",
                        FieldValue.arrayUnion(newMe)
                    )
                }
                null
            }
                .addOnCompleteListener {
                    onResult(it.exception)
                }
        }

    }
}

data class UserWithFriends(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val propicUrl: String = "",
    val friends: List<User> = emptyList(),
)

data class FriendshipRequest(
    val from: User = User(),
    val to: User = User()
)

fun UserWithFriends.toUser(): User {
    return User(
        id = id,
        name = name,
        username = username,
        propicUrl = propicUrl
    )
}
