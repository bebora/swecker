package dev.bebora.swecker.data.service.impl

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.bebora.swecker.data.StoredAlarm
import dev.bebora.swecker.data.ThinGroup
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class AlarmProviderServiceImpl : AlarmProviderService {
    override fun getUserGroups(userId: String): Flow<List<ThinGroup>> {
        if (userId.isBlank()) {
            Log.d("SWECKER-EMPTY-USER", "Empty user id")
            return MutableStateFlow(emptyList<ThinGroup>()).asStateFlow()
        } else {
            return callbackFlow {
                val listener = Firebase.firestore
                    .collection(FirebaseConstants.GROUPS_COLLECTION)
                    .whereArrayContains("members", userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.d("SWECKER-LISTEN-GRP", "Cannot retrieve groups", error)
                            return@addSnapshotListener
                        }
                        if (snapshot != null) {
                            Log.d("SWECKER-GET-GRP-EXISTS", "Current data: $snapshot")
                            trySend(
                                snapshot.toObjects(ThinGroup::class.java)
                            )
                        } else {
                            Log.d("SWECKER-GET-GRP-NOPE", "Current data: null")
                            trySend(emptyList())
                        }
                    }
                awaitClose {
                    listener.remove()
                }
            }
        }
    }

    override fun getUserChannels(userId: String): Flow<List<ThinGroup>> {
        if (userId.isBlank()) {
            Log.d("SWECKER-EMPTY-USER", "Empty user id")
            return MutableStateFlow(emptyList<ThinGroup>()).asStateFlow()
        } else {
            return callbackFlow {
                val listener = Firebase.firestore
                    .collection(FirebaseConstants.CHANNELS_COLLECTION)
                    .whereArrayContains("members", userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.d("SWECKER-LISTEN-CH", "Cannot retrieve channels", error)
                            return@addSnapshotListener
                        }
                        if (snapshot != null) {
                            Log.d("SWECKER-GET-CH-EXISTS", "Current data: $snapshot")
                            trySend(
                                snapshot.toObjects(ThinGroup::class.java)
                            )
                        } else {
                            Log.d("SWECKER-GET-CH-NOPE", "Current data: null")
                            trySend(emptyList())
                        }
                    }
                awaitClose {
                    listener.remove()
                }
            }
        }
    }

    override fun createGroup(
        ownerId: String,
        userIds: List<String>, // The list should already include the owner
        onSuccess: (ThinGroup) -> Unit, // Contains id of created group
        onFailure: (Throwable) -> Unit
    ) {
        if (userIds.isEmpty()) {
            onFailure(EmptyGroupException())
        } else {
            val newDocRef = Firebase.firestore
                .collection(FirebaseConstants.GROUPS_COLLECTION)
                .document()
            val newGroup = ThinGroup(
                id = newDocRef.id,
                members = userIds,
                name = "Group name",
                owner = ownerId,
                picture = ""
            )
            newDocRef.set(
                newGroup
            ).addOnFailureListener(onFailure)
                .addOnSuccessListener {
                    onSuccess(newGroup)
                }
        }
    }

    override fun updateGroup(newGroupData: ThinGroup, onComplete: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(FirebaseConstants.GROUPS_COLLECTION)
            .document(newGroupData.id)
            .set(newGroupData)
            .addOnCompleteListener {
                onComplete(it.exception)
            }
    }

    //TODO should the alarms be removed after group deletion? And the chats?
    override fun deleteGroup(groupId: String, onComplete: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(FirebaseConstants.GROUPS_COLLECTION)
            .document(groupId)
            .delete()
            .addOnCompleteListener {
                onComplete(it.exception)
            }
    }

    override fun createChannel(
        channel: ThinGroup,
        onComplete: (Throwable?) -> Unit
    ) {
        if (channel.id.isBlank()) {
            onComplete(EmptyChannelException())
        } else if (channel.handle == null) {
            onComplete(EmptyHandleException())
        } else if (channel.members.isEmpty()) {
            onComplete(EmptyGroupException())
        } else {
            val newGroup = channel.copy(
                lowerName = channel.name.lowercase().trim(),
                handle = channel.handle.lowercase().trim()
            )
            Firebase.firestore
                .collection(FirebaseConstants.CHANNELS_COLLECTION)
                .document(channel.id)
                .set(newGroup)
                .addOnCompleteListener {
                    onComplete(it.exception)
                }
        }
    }

    override fun updateChannel(newChannelData: ThinGroup, onComplete: (Throwable?) -> Unit) {
        if (newChannelData.id.isBlank()) {
            onComplete(EmptyChannelException())
        } else if (newChannelData.handle == null) {
            onComplete(EmptyHandleException())
        } else if (newChannelData.members.isEmpty()) {
            onComplete(EmptyGroupException())
        } else {
            Firebase.firestore
                .collection(FirebaseConstants.CHANNELS_COLLECTION)
                .document(newChannelData.id)
                .set(
                    newChannelData.copy(
                        lowerName = newChannelData.name.lowercase(),
                        handle = newChannelData.handle.lowercase()
                    )
                )
                .addOnCompleteListener {
                    onComplete(it.exception)
                }
        }
    }

    //TODO should the alarms be removed after group deletion? And the chats?
    override fun deleteChannel(channelId: String, onComplete: (Throwable?) -> Unit) {
        if (channelId.isBlank()) {
            onComplete(EmptyChannelException())
        } else {
            Firebase.firestore
                .collection(FirebaseConstants.CHANNELS_COLLECTION)
                .document(channelId)
                .delete()
                .addOnCompleteListener {
                    onComplete(it.exception)
                }
        }
    }

    override fun searchNewChannels(
        from: User,
        query: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<ThinGroup>) -> Unit
    ) {
        Log.d("SWECKER-SEARCH-CH", "Searching for new channels starting with '$query'")
        if (query.isEmpty()) {
            onSuccess(emptyList())
        } else {
            val lowerQuery = query.lowercase()
            val channelsRef = Firebase.firestore
                .collection(FirebaseConstants.CHANNELS_COLLECTION)

            channelsRef
                .whereGreaterThanOrEqualTo("handle", lowerQuery)
                .whereLessThanOrEqualTo(
                    "handle",
                    "${lowerQuery}~"
                ) // https://stackoverflow.com/questions/46568142/google-firestore-query-on-substring-of-a-property-value-text-search
                .get()
                .addOnFailureListener(onError)
                .addOnSuccessListener { handlesQuerySnapshot ->
                    channelsRef
                        .whereGreaterThanOrEqualTo("lowerName", lowerQuery)
                        .whereLessThanOrEqualTo("lowerName", "${lowerQuery}~")
                        .get()
                        .addOnFailureListener(onError)
                        .addOnSuccessListener { namesQuerySnapshot ->
                            val handleResults =
                                handlesQuerySnapshot.toObjects(ThinGroup::class.java)
                                    .filter { !it.members.contains(from.id) }
                            val nameResults = namesQuerySnapshot.toObjects(ThinGroup::class.java)
                                .filter { !it.members.contains(from.id) }
                            val handleResultSet = handleResults.map { it.id }.toSet()
                            val mergedResults =
                                handleResults + nameResults.filter { !handleResultSet.contains(it.id) }
                            onSuccess(
                                mergedResults
                            )
                        }
                }
        }
    }

    override fun joinChannel(userId: String, channelId: String, onComplete: (Throwable?) -> Unit) {
        if (userId.isBlank()) {
            onComplete(EmptyUserException())
        } else if (channelId.isBlank()) {
            onComplete(EmptyChannelException())
        } else {
            Firebase.firestore
                .collection(FirebaseConstants.CHANNELS_COLLECTION)
                .document(channelId)
                .update("members", FieldValue.arrayUnion(userId))
                .addOnCompleteListener {
                    onComplete(it.exception)
                }
        }
    }

    override fun leaveChannel(userId: String, channelId: String, onComplete: (Throwable?) -> Unit) {
        if (userId.isBlank()) {
            onComplete(EmptyUserException())
        } else if (channelId.isBlank()) {
            onComplete(EmptyChannelException())
        } else {
            Firebase.firestore
                .collection(FirebaseConstants.CHANNELS_COLLECTION)
                .document(channelId)
                .update("members", FieldValue.arrayRemove(userId))
                .addOnCompleteListener {
                    onComplete(it.exception)
                }
        }
    }

    override fun leaveGroup(userId: String, groupId: String, onComplete: (Throwable?) -> Unit) {
        if (userId.isBlank()) {
            onComplete(EmptyUserException())
        } else if (groupId.isBlank()) {
            onComplete(EmptyChannelException())
        } else {
            Firebase.firestore
                .collection(FirebaseConstants.GROUPS_COLLECTION)
                .document(groupId)
                .update("members", FieldValue.arrayRemove(userId))
                .addOnCompleteListener {
                    onComplete(it.exception)
                }
        }
    }

    override fun createAlarm(alarm: StoredAlarm, onComplete: (Throwable?) -> Unit) {
        if (alarm.groupId == null) { // Create an alarm just for me
            Firebase.firestore
                .collection(FirebaseConstants.ALARMS_COLLECTION)
                .add(alarm)
                .addOnCompleteListener { onComplete(it.exception) }
        } else { // Create alarm in group and for everyone in the group
            val store = Firebase.firestore
            val alarmsRef = store.collection(FirebaseConstants.ALARMS_COLLECTION)
            val groupsRef = store.collection(FirebaseConstants.GROUPS_COLLECTION)
            val groupRef = groupsRef.document(alarm.groupId)
            store.runTransaction { transaction ->
                val groupInDb = transaction.get(groupRef)
                val members = groupInDb.toObject(ThinGroup::class.java)?.members ?: emptyList()
                transaction.set(
                    alarmsRef.document(alarm.id), // In the original alarm the id field and the document id are the same
                    alarm
                )
                members.forEach { memberId ->
                    transaction.set(
                        alarmsRef.document(),
                        alarm.copy(userId = memberId)
                    )
                }
                null
            }
                .addOnCompleteListener {
                    onComplete(it.exception)
                }
        }
    }

    override fun getUserAlarms(userId: String): Flow<List<StoredAlarm>> {
        if (userId.isBlank()) {
            Log.d("SWECKER-EMPTY-USER", "Empty user id")
            return MutableStateFlow(emptyList<StoredAlarm>()).asStateFlow()
        } else {
            return callbackFlow {
                val listener = Firebase.firestore
                    .collection(FirebaseConstants.ALARMS_COLLECTION)
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.d("SWECKER-LISTEN-ALARM", "Cannot retrieve alarms", error)
                            return@addSnapshotListener
                        }
                        if (snapshot != null) {
                            Log.d("SWECKER-GET-ALR-EXISTS", "Current data: $snapshot")
                            trySend(
                                snapshot.toObjects(StoredAlarm::class.java)
                            )
                        } else {
                            Log.d("SWECKER-GET-ALR-NOPE", "Current data: null")
                            trySend(emptyList())
                        }
                    }
                awaitClose {
                    listener.remove()
                }
            }
        }
    }

    override fun updateAlarm(alarm: StoredAlarm, onComplete: (Throwable?) -> Unit) {
        if (alarm.userId != null) { // Update just my version
            Firebase.firestore
                .collection(FirebaseConstants.ALARMS_COLLECTION)
                .whereEqualTo("userId", alarm.userId)
                .whereEqualTo("id", alarm.id)
                .get() //Get the actual document, my id is not real
                .addOnFailureListener(onComplete)
                .addOnSuccessListener { querySnapshot ->
                    val documentId = querySnapshot.documents[0].id
                    Firebase.firestore
                        .collection(FirebaseConstants.ALARMS_COLLECTION)
                        .document(documentId)
                        .set(alarm)
                        .addOnCompleteListener {
                            onComplete(it.exception)
                        }
                }
        } else { // I need to update all the subscribers that are receiving updates on this alarm with the same "id" field
            val store = Firebase.firestore
            val alarmsRef = Firebase.firestore
                .collection(FirebaseConstants.ALARMS_COLLECTION)
            alarmsRef
                .whereEqualTo("id", alarm.id)
                .whereEqualTo("receiveUpdates", true)
                .whereEqualTo("enabled", true)
                .get()
                .addOnFailureListener(onComplete)
                .addOnSuccessListener { querySnapshot ->
                    val docIdAndUserId: List<Pair<String, String>> = querySnapshot.documents.map {
                        Pair(
                            it.id,
                            (it.data?.get("userId") ?: "error!") as String
                        )
                    }
                    store.runTransaction { transaction ->
                        docIdAndUserId.forEach { docIdUserId ->
                            transaction.set(
                                alarmsRef.document(docIdUserId.first),
                                alarm.copy(
                                    userId = docIdUserId.second
                                )
                            )
                        }
                        null
                    }.addOnCompleteListener {
                        onComplete(it.exception)
                    }
                }
        }
    }
}
