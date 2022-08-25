package dev.bebora.swecker.data.service.testimpl

import android.net.Uri
import dev.bebora.swecker.data.service.ImageStorageService

class FakeImageStorageService : ImageStorageService {
    override fun setProfilePicture(
        userId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val waterfall = "https://www.gstatic.com/webp/gallery/2.jpg"
        onSuccess(waterfall)
    }

    override fun setGroupPicture(
        groupId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val field = "https://filesamples.com/samples/image/jpeg/sample_640%C3%97426.jpeg"
        onSuccess(field)
    }

    override fun deleteGroupPicture(
        groupId: String,
        onComplete: (Throwable?) -> Unit
    ) {
        onComplete(null)
    }

    override fun setChannelPicture(
        channelId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val fire = "https://www.gstatic.com/webp/gallery/5.jpg"
        onSuccess(fire)
    }

    override fun deleteChannelPicture(
        channelId: String,
        onComplete: (Throwable?) -> Unit
    ) {
        onComplete(null)
    }
}
