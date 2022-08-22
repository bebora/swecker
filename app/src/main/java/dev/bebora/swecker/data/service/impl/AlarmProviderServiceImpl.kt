package dev.bebora.swecker.data.service.impl

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.bebora.swecker.data.ThinGroup
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.EmptyGroupException
import kotlinx.coroutines.flow.Flow

class AlarmProviderServiceImpl : AlarmProviderService {
    override fun getUserGroups(userId: String): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override fun createGroup(
        ownerId: String,
        userIds: List<String>,
        onSuccess: (String) -> Unit, // Id of created group
        onFailure: (Throwable) -> Unit
    ) {
        if (userIds.isEmpty()) {
            onFailure(EmptyGroupException())
        } else {
            val newDocRef = Firebase.firestore
                .collection(FirebaseConstants.GROUPS_COLLECTION)
                .document()
            newDocRef.set(
                ThinGroup(
                    id = newDocRef.id,
                    members = userIds,
                    name = "Group name",
                    owner = ownerId,
                    picture = ""
                )
            ).addOnFailureListener(onFailure)
                .addOnSuccessListener {
                    onSuccess(newDocRef.id)
                }
        }
    }
}
