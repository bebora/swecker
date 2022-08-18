package dev.bebora.swecker.data.service

interface ImageStorageService {
    fun getProfilePictureUrl(
        userId: String,
        onSuccess: (String) -> Unit
    )
}
