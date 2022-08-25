package dev.bebora.swecker.data.service.testimpl

import dev.bebora.swecker.data.StoredAlarm
import dev.bebora.swecker.data.ThinGroup
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.AlarmProviderService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlarmProviderServiceTestImpl : AlarmProviderService {
    override fun getUserGroups(userId: String): Flow<List<ThinGroup>> {
        return MutableStateFlow(emptyList<ThinGroup>()).asStateFlow()
    }

    override fun getUserChannels(userId: String): Flow<List<ThinGroup>> {
        return MutableStateFlow(emptyList<ThinGroup>()).asStateFlow()
    }

    override fun createGroup(
        ownerId: String,
        userIds: List<String>,
        onSuccess: (ThinGroup) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        onSuccess(ThinGroup())
    }

    override fun updateGroup(newGroupData: ThinGroup, onComplete: (Throwable?) -> Unit) {
        onComplete(null)
    }

    override fun deleteGroup(groupId: String, onComplete: (Throwable?) -> Unit) {
        onComplete(null)
    }

    override fun createChannel(
        channel: ThinGroup,
        onComplete: (Throwable?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateChannel(newChannelData: ThinGroup, onComplete: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteChannel(channelId: String, onComplete: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun searchNewChannels(
        from: User,
        query: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<ThinGroup>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun joinChannel(userId: String, channelId: String, onComplete: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun leaveChannel(userId: String, channelId: String, onComplete: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun leaveGroup(userId: String, groupId: String, onComplete: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun createAlarm(alarm: StoredAlarm, onComplete: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getUserAlarms(userId: String): Flow<List<StoredAlarm>> {
        return MutableStateFlow(emptyList<StoredAlarm>()).asStateFlow()
    }

    override fun updateAlarm(alarm: StoredAlarm, onComplete: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    /**
     * This function will soft delete the alarm and set a flag
     */
    override fun deleteAlarm(alarm: StoredAlarm, onComplete: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }
}
