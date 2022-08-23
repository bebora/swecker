package dev.bebora.swecker.data.service.testimpl

import dev.bebora.swecker.data.StoredAlarm
import dev.bebora.swecker.data.ThinGroup
import dev.bebora.swecker.data.service.AlarmProviderService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlarmProviderServiceTestImpl : AlarmProviderService {
    override fun getUserGroups(userId: String): Flow<List<ThinGroup>> {
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

    override fun createAlarm(alarm: StoredAlarm, onComplete: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getUserAlarms(userId: String): Flow<List<StoredAlarm>> {
        return MutableStateFlow(emptyList<StoredAlarm>()).asStateFlow()
    }
}
