package dev.bebora.swecker.data.service.testimpl

import dev.bebora.swecker.data.ThinGroup
import dev.bebora.swecker.data.service.AlarmProviderService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class AlarmProviderServiceTestImpl : AlarmProviderService {
    override fun getUserGroups(userId: String): Flow<List<ThinGroup>> {
        return emptyFlow()
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
}
