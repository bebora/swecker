package dev.bebora.swecker.data.service.testimpl

import dev.bebora.swecker.data.StoredAlarm
import dev.bebora.swecker.data.ThinGroup
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.AlarmProviderService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class FakeAlarmProviderService : AlarmProviderService {
    var groups = mutableMapOf<String, ThinGroup>()
    var channels = mutableMapOf<String, ThinGroup>()
    var alarms = mutableListOf<StoredAlarm>()


    override fun getUserGroups(userId: String): Flow<List<ThinGroup>> {
        return flow {
            emit(groups
                .filterValues { it.members.contains(userId) }
                .map { it.value })
        }
    }

    override fun getUserChannels(userId: String): Flow<List<ThinGroup>> {
        return flow {
            emit(channels
                .filterValues { it.members.contains(userId) }
                .map { it.value })
        }
    }

    override fun createGroup(
        ownerId: String,
        userIds: List<String>,
        onSuccess: (ThinGroup) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val randomId = UUID.randomUUID().toString()
        val newGroup = ThinGroup(
            id = randomId,
            members = userIds,
            owner = ownerId
        )
        groups[randomId] = newGroup
        onSuccess(newGroup)
    }

    // Test do not notify listeners
    override fun updateGroup(newGroupData: ThinGroup, onComplete: (Throwable?) -> Unit) {
        groups[newGroupData.id] = newGroupData
    }

    // Test do not notify listeners
    override fun deleteGroup(groupId: String, onComplete: (Throwable?) -> Unit) {
        groups.remove(groupId)
    }

    // Test do not notify listeners
    override fun createChannel(
        channel: ThinGroup,
        onComplete: (Throwable?) -> Unit
    ) {
        channels[channel.id] = channel
    }

    // Test do not notify listeners
    override fun updateChannel(newChannelData: ThinGroup, onComplete: (Throwable?) -> Unit) {
        channels[newChannelData.id] = newChannelData
    }

    // Test do not notify listeners
    override fun deleteChannel(channelId: String, onComplete: (Throwable?) -> Unit) {
        channels.remove(channelId)
    }

    override fun searchNewChannels(
        from: User,
        query: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<ThinGroup>) -> Unit
    ) {
        val result = channels.filterValues {
            (it.lowerName!!.startsWith(query) || it.handle!!.startsWith(query)) &&
                    !it.members.contains(from.id)
        }
        onSuccess(result.values.toList())
    }

    override fun joinChannel(userId: String, channelId: String, onComplete: (Throwable?) -> Unit) {
        // TODO handle not existing channel
        val originChannel = channels[channelId]
        channels[channelId] = originChannel!!.copy(
            members = originChannel.members.plus(userId)
        )
        onComplete(null)
    }

    override fun leaveChannel(userId: String, channelId: String, onComplete: (Throwable?) -> Unit) {
        // TODO handle not existing channel
        val originChannel = channels[channelId]
        channels[channelId] = originChannel!!.copy(
            members = originChannel.members.minus(userId)
        )
        onComplete(null)
    }

    override fun leaveGroup(userId: String, groupId: String, onComplete: (Throwable?) -> Unit) {
        // TODO handle not existing channel
        val originGroup = groups[groupId]
        groups[groupId] = originGroup!!.copy(
            members = originGroup.members.minus(userId)
        )
        onComplete(null)
    }

    // Fake implementation does not create alarms for others
    override fun createAlarm(alarm: StoredAlarm, onComplete: (Throwable?) -> Unit) {
        assert(alarm.id.isNotEmpty())
        alarms.add(alarm)
    }

    override fun getUserAlarms(userId: String): Flow<List<StoredAlarm>> {
        return flow {
            emit(alarms.filter { it.userId == userId })
        }
    }

    // Fake implementation does not update alarms for others
    override fun updateAlarm(alarm: StoredAlarm, onComplete: (Throwable?) -> Unit) {
        val oldAlarmIndex = alarms.indexOfFirst {
            it.id == alarm.id && it.userId == alarm.userId
        }
        alarms.removeAt(oldAlarmIndex)
        alarms.add(alarm)
    }

    override fun deleteAlarm(alarm: StoredAlarm, onComplete: (Throwable?) -> Unit) {
        val oldAlarmIndex = alarms.indexOfFirst {
            it.id == alarm.id && it.userId == alarm.userId
        }
        alarms.removeAt(oldAlarmIndex)
    }
}
