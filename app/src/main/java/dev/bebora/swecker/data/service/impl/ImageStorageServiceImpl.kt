package dev.bebora.swecker.data.service.impl

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
}
