package dev.bebora.swecker.data.service.impl

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.bebora.swecker.data.service.ImageStorageService

class ImageStorageServiceImpl : ImageStorageService {
    override fun getProfilePictureUrl(userId: String, onSuccess: (String) -> Unit) {
        Firebase.storage.reference
            .child("images/${userId}")
            .downloadUrl.addOnSuccessListener {
                onSuccess(it.toString())
            }
    }

    override fun setProfilePicture(
        userId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.d("SWECKER-SELECT-FILE", imageUri.toString())
        val storageRef = Firebase.storage.reference
        val propicRef = storageRef.child("images/${userId}")
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
}
