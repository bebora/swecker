package dev.bebora.swecker.data.service

import android.net.Uri

interface ImageStorageService {
    /*fun getProfilePictureUrl(
        userId: String,
        onSuccess: (String) -> Unit
    )*/
    fun setProfilePicture(
        userId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    )

    fun setGroupPicture(
        groupId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    )
}
