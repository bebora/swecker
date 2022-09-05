package dev.bebora.swecker.data.service.impl

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.bebora.swecker.data.service.ImageStorageService

class ImageStorageServiceImpl : ImageStorageService {
    override fun setProfilePicture(
        userId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.d("SWECKER-SELECT-FILE-PR", imageUri.toString())
        val storageRef = Firebase.storage.reference
        val propicRef = storageRef.child("${FirebaseConstants.PICTURES_USERS}/${userId}")
        val uploadTask = propicRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Log.d("SWECKER-UPLOAD-PIC", "Cannot upload new image")
                onFailure(task.exception.toString())
            }
            propicRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess(task.result.toString())
            } else {
                Log.d("SWECKER-GETURI", "Cannot get new image uri")
                onFailure(task.exception.toString())
            }
        }
    }

    override fun setGroupPicture(
        groupId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.d("SWECKER-SELECT-FILE-GR", imageUri.toString())
        val storageRef = Firebase.storage.reference
        val groupPicRef = storageRef.child("${FirebaseConstants.PICTURES_GROUPS}/${groupId}")
        val uploadTask = groupPicRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Log.d("SWECKER-GROUP-PIC", "Cannot upload new image")
                onFailure(task.exception.toString())
            }
            groupPicRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess(task.result.toString())
            } else {
                Log.d("SWECKER-GETURI-GR", "Cannot get new image uri")
                onFailure(task.exception.toString())
            }
        }
    }

    override fun deleteGroupPicture(
        groupId: String,
        onComplete: (Throwable?) -> Unit
    ) {
        val storageRef = Firebase.storage.reference
        val groupPicRef = storageRef.child("${FirebaseConstants.PICTURES_GROUPS}/${groupId}")
        groupPicRef
            .delete()
            .addOnCompleteListener {
                onComplete(it.exception)
            }
    }

    override fun setChannelPicture(
        channelId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.d("SWECKER-SELECT-FILE-CH", imageUri.toString())
        val storageRef = Firebase.storage.reference
        val channelPicRef = storageRef.child("${FirebaseConstants.PICTURES_CHANNELS}/${channelId}")
        val uploadTask = channelPicRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Log.d("SWECKER-CHAN-PIC", "Cannot upload new image")
                onFailure(task.exception.toString())
            }
            channelPicRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess(task.result.toString())
            } else {
                Log.d("SWECKER-GETURI-CH", "Cannot get new image uri")
                onFailure(task.exception.toString())
            }
        }
    }

    override fun deleteChannelPicture(
        channelId: String,
        onComplete: (Throwable?) -> Unit
    ) {
        val storageRef = Firebase.storage.reference
        val channelPicRef = storageRef.child("${FirebaseConstants.PICTURES_CHANNELS}/${channelId}")
        channelPicRef
            .delete()
            .addOnCompleteListener {
                onComplete(it.exception)
            }
    }
}
