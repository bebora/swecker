package dev.bebora.swecker.data.service

import dev.bebora.swecker.data.StoredAlarm
import dev.bebora.swecker.data.ThinGroup
import kotlinx.coroutines.flow.Flow

interface AlarmProviderService {
    fun getUserGroups(userId: String): Flow<List<ThinGroup>>
    fun createGroup(
        ownerId: String,
        userIds: List<String>,
        onSuccess: (ThinGroup) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    fun updateGroup(
        newGroupData: ThinGroup,
        onComplete: (Throwable?) -> Unit
    )

    fun deleteGroup(
        groupId: String,
        onComplete: (Throwable?) -> Unit
    )

    fun createAlarm(
        alarm: StoredAlarm,
        onComplete: (Throwable?) -> Unit
    )

    fun getUserAlarms(userId: String): Flow<List<StoredAlarm>>

    fun updateAlarm(
        alarm: StoredAlarm,
        onComplete: (Throwable?) -> Unit
    )
    // TODO add a way to set chat enabled/disabled
}

class EmptyGroupException : Exception()
