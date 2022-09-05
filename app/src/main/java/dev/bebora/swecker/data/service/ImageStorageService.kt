package dev.bebora.swecker.data.service

import android.net.Uri

interface ImageStorageService {
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

    fun deleteGroupPicture(
        groupId: String,
        onComplete: (Throwable?) -> Unit
    )

    fun setChannelPicture(
        channelId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    )

    fun deleteChannelPicture(
        channelId: String,
        onComplete: (Throwable?) -> Unit
    )
}
