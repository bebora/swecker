package dev.bebora.swecker.data.service.impl

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.bebora.swecker.data.StoredAlarm
import dev.bebora.swecker.data.ThinGroup
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.EmptyGroupException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow

class AlarmProviderServiceImpl : AlarmProviderService {
    override fun getUserGroups(userId: String): Flow<List<ThinGroup>> {
        if (userId.isBlank()) {
            Log.d("SWECKER-EMPTY-USER", "Empty user id")
            return emptyFlow()
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
        userIds: List<String>,
        onSuccess: (ThinGroup) -> Unit, // Id of created group
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

    override fun deleteGroup(groupId: String, onComplete: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(FirebaseConstants.GROUPS_COLLECTION)
            .document(groupId)
            .delete()
            .addOnCompleteListener {
                onComplete(it.exception)
            }
    }

    override fun createAlarm(alarm: StoredAlarm, onComplete: (Throwable?) -> Unit) { //TODO handle alarm id when not provided
        if (alarm.groupId == null) { // Create an alarm just for me
            Firebase.firestore
                .collection(FirebaseConstants.ALARMS)
                .add(alarm)
                .addOnCompleteListener { onComplete(it.exception) }
        } else { // Create alarm in group and for everyone in the group
            val store = Firebase.firestore
            val alarmsRef = store.collection(FirebaseConstants.ALARMS)
            val groupsRef = store.collection(FirebaseConstants.GROUPS_COLLECTION)
            val groupRef = groupsRef.document(alarm.groupId)
            store.runTransaction { transaction ->
                val groupInDb = transaction.get(groupRef)
                val members = groupInDb.toObject(ThinGroup::class.java)?.members ?: emptyList()
                transaction.set(
                    alarmsRef.document(),
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
            return emptyFlow()
        } else {
            return callbackFlow {
                val listener = Firebase.firestore
                    .collection(FirebaseConstants.ALARMS)
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
}
