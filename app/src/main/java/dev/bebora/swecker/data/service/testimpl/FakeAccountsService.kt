package dev.bebora.swecker.data.service.testimpl

import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.*
import dev.bebora.swecker.data.service.impl.UserWithFriends
import dev.bebora.swecker.data.service.impl.toUser
import kotlinx.coroutines.flow.*

class FakeAccountsService(
    initialUsers: MutableMap<String, UserWithFriends> = mutableMapOf(),
    initialFriendshipRequests: MutableMap<String, List<String>> = mutableMapOf()
) : AccountsService {
    private val users = initialUsers
    private val friendshipRequests = initialFriendshipRequests // to, from
    private val userUpdates = MutableStateFlow(0)
    private val friendshipRequestsUpdates = MutableStateFlow(0)

    override fun getUser(userId: String, onError: (Throwable) -> Unit, onSuccess: (User) -> Unit) {
        if (userId.isBlank()) {
            onError(BlankUserOrUsernameException())
        } else {
            val user = users[userId]
            onSuccess(user?.toUser() ?: User())
        }
    }

    override fun saveUser(requestedUser: User, oldUser: User?, onResult: (Throwable?) -> Unit) {
        var user = requestedUser.copy(
            username = requestedUser.username.lowercase()
        )
        if (user.name.isBlank() && user.username.isBlank()) {
            user = user.copy(name = user.id, username = user.id.lowercase())
        } else if (user.name.isBlank() || user.username.isBlank()) {
            onResult(BlankUserOrUsernameException())
            return
        }
        val usersWithSameUsername =
            users.filterValues { it.username == requestedUser.username && it.id != requestedUser.id }
        if (usersWithSameUsername.isNotEmpty()) {
            onResult(UsernameAlreadyTakenException())
        } else {
            val userInStorage = users[requestedUser.id]
            if (userInStorage == null) {
                users[requestedUser.id] = UserWithFriends(
                    id = user.id,
                    name = user.name,
                    username = user.username,
                    propicUrl = user.propicUrl,
                    friends = emptyList()
                )
            } else {
                users[requestedUser.id] = userInStorage.copy(
                    id = user.id,
                    name = user.name,
                    username = user.username,
                    propicUrl = user.propicUrl, // Skip setting friend
                )
            }
            userUpdates.value += 1
            onResult(null)
            // In this fake implementation, friends will not be notified of changes
        }
    }

    override fun getFriends(userId: String): Flow<List<User>> {
        return if (userId.isBlank()) {
            flow { emit(emptyList()) }
        } else {
            userUpdates.asStateFlow().map {
                users[userId]?.friends ?: emptyList()
            }
        }
    }

    override fun requestFriendship(from: User, to: User, onResult: (Throwable?) -> Unit) {
        if (from.id == to.id) {
            onResult(FriendshipRequestToYourselfException())
            return
        }
        val requestExist = friendshipRequests[to.id]?.any { it == from.id } ?: false
        if (requestExist) {
            onResult(FriendshipRequestAlreadySentException())
        } else {
            friendshipRequests[to.id] = (friendshipRequests[to.id] ?: emptyList()) + listOf(from.id)
            friendshipRequestsUpdates.value += 1
            onResult(null)
        }
    }

    override fun acceptFriendship(me: User, newFriend: User, onResult: (Throwable?) -> Unit) {
        val requestToMe = friendshipRequests[me.id]
        if (requestToMe == null || !requestToMe.any { it == newFriend.id }) {
            onResult(FriendshipRequestNotExistingException())
        } else {
            val updatedRequestsToMe = requestToMe - newFriend.id
            friendshipRequests[me.id] = updatedRequestsToMe
            // Beware that request should only be sent from and to valid users
            val meInDb = users[me.id]!!
            val friendInDb = users[newFriend.id]!!
            users[me.id] = meInDb.copy(
                friends = meInDb.friends + newFriend
            )
            users[newFriend.id] = friendInDb.copy(
                friends = friendInDb.friends + me
            )
            friendshipRequestsUpdates.value += 1
            userUpdates.value += 1
            onResult(null)
        }
    }

    override fun getFriendshipRequests(userId: String): Flow<List<User>> {
        if (userId.isBlank()) {
            return flow { emit(emptyList()) }
        }
        val requesterIds = friendshipRequests[userId] ?: emptyList()
        return friendshipRequestsUpdates.asStateFlow().map {
            requesterIds.mapNotNull { users[it]?.toUser() }
        }
    }

    override fun searchUsers(
        from: User,
        query: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<User>) -> Unit
    ) {
        if (query.isEmpty()) {
            onSuccess(emptyList())
        } else {
            onSuccess(users.values
                .filter { it.username.startsWith(query) && it.username != from.username }
                .map { it.toUser() })
        }
    }

    override fun removeFriend(me: User, friend: User, onResult: (Throwable?) -> Unit) {
        if (me.id.isBlank() ||
            friend.id.isBlank() ||
            !users.containsKey(me.id) ||
            !users.containsKey(
                friend.id
            )
        ) {
            onResult(UserNotFoundException())
        } else {
            // Beware that request should only be sent from and to valid users
            val meInDb = users[me.id]!!
            val friendInDb = users[friend.id]!!
            users[me.id] = meInDb.copy(
                friends = meInDb.friends.filter { it.id != friend.id }
            )
            users[friend.id] = friendInDb.copy(
                friends = friendInDb.friends.filter { it.id != me.id }
            )
            userUpdates.value += 1
            onResult(null)
        }
    }
}
