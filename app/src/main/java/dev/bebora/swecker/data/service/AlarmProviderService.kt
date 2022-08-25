package dev.bebora.swecker.data.service

import dev.bebora.swecker.data.StoredAlarm
import dev.bebora.swecker.data.ThinGroup
import dev.bebora.swecker.data.User
import kotlinx.coroutines.flow.Flow

interface AlarmProviderService {
    fun getUserGroups(userId: String): Flow<List<ThinGroup>>
    fun getUserChannels(userId: String): Flow<List<ThinGroup>>
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

    fun createChannel(
        channel: ThinGroup, // The channel should already have an id!
        onComplete: (Throwable?) -> Unit
    )

    fun updateChannel(
        newChannelData: ThinGroup,
        onComplete: (Throwable?) -> Unit
    )

    fun deleteChannel(
        channelId: String,
        onComplete: (Throwable?) -> Unit
    )

    fun searchNewChannels(
        from: User,
        query: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<ThinGroup>) -> Unit
    )

    fun joinChannel(
        userId: String,
        channelId: String,
        onComplete: (Throwable?) -> Unit,
    )

    fun leaveChannel(
        userId: String,
        channelId: String,
        onComplete: (Throwable?) -> Unit
    )

    fun leaveGroup(
        userId: String,
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

    fun deleteAlarm(
        alarm: StoredAlarm,
        onComplete: (Throwable?) -> Unit
    )
}

class EmptyGroupException : Exception()
class EmptyUserException : Exception()
class EmptyChannelException : Exception() // Channel id is blank
class EmptyHandleException : Exception() // Channel should contain valid handle
