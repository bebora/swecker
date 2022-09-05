package dev.bebora.swecker.data.service

import dev.bebora.swecker.data.User
import kotlinx.coroutines.flow.Flow

interface AccountsService {
    fun getUser(
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (User) -> Unit
    )

    /**
     * If update is true, the existing user will be updated, otherwise it will be created
     */
    fun saveUser(requestedUser: User, oldUser: User?, onResult: (Throwable?) -> Unit)
    fun getFriends(userId: String): Flow<List<User>>
    fun requestFriendship(from: User, to: User, onResult: (Throwable?) -> Unit)
    fun acceptFriendship(me: User, newFriend: User, onResult: (Throwable?) -> Unit)
    fun getFriendshipRequests(userId: String): Flow<List<User>>
    fun searchUsers(
        from: User,
        query: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<User>) -> Unit
    )

    fun removeFriend(me: User, friend: User, onResult: (Throwable?) -> Unit)
}

class UsernameAlreadyTakenException() : Exception()
class BlankUserOrUsernameException() : Exception()
class FriendshipRequestAlreadySentException() : Exception()
class FriendshipRequestToYourselfException() : Exception()
class FriendshipRequestNotExistingException() : Exception()
class UserNotFoundException() : Exception()
