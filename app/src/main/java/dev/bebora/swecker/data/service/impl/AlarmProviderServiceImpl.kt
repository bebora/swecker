package dev.bebora.swecker.data.service.impl

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.bebora.swecker.data.StoredAlarm
import dev.bebora.swecker.data.ThinGroup
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.EmptyGroupException
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
        ownerId: String,
        userIds: List<String>, // The list should already include the owner
        onSuccess: (ThinGroup) -> Unit, // Contains id of created channel
        onFailure: (Throwable) -> Unit
    ) {
        if (userIds.isEmpty()) {
            onFailure(EmptyGroupException())
        } else {
            val newDocRef = Firebase.firestore
                .collection(FirebaseConstants.CHANNELS_COLLECTION)
                .document()
            val newGroup = ThinGroup(
                id = newDocRef.id,
                members = userIds,
                name = "Channel name",
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

    override fun updateChannel(newChannelData: ThinGroup, onComplete: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(FirebaseConstants.CHANNELS_COLLECTION)
            .document(newChannelData.id)
            .set(newChannelData)
            .addOnCompleteListener {
                onComplete(it.exception)
            }
    }

    //TODO should the alarms be removed after group deletion? And the chats?
    override fun deleteChannel(channelId: String, onComplete: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(FirebaseConstants.CHANNELS_COLLECTION)
            .document(channelId)
            .delete()
            .addOnCompleteListener {
                onComplete(it.exception)
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
