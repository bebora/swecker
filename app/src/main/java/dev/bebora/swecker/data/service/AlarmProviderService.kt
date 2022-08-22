package dev.bebora.swecker.data.service

import kotlinx.coroutines.flow.Flow

interface AlarmProviderService {
    fun getUserGroups(userId: String): Flow<List<String>>
    fun createGroup(
        ownerId: String,
        userIds: List<String>,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit
    )
}

class EmptyGroupException : Exception()
