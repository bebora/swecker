package dev.bebora.swecker.data.service

import dev.bebora.swecker.data.User
import kotlinx.coroutines.flow.Flow

interface AccountsService {
    fun getUser(
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (User) -> Unit
    )

    //fun addUserListener()

    /**
     * If update is true, the existing user will be updated, otherwise it will be created
     */
    fun saveUser(requestedUser: User, oldUser: User?, onResult: (Throwable?) -> Unit)
    // fun updateUser(user: User, onResult: (Throwable?) -> Unit)

    /*fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Task) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun removeListener()
    fun getTask(taskId: String, onError: (Throwable) -> Unit, onSuccess: (Task) -> Unit)
    fun saveTask(task: Task, onResult: (Throwable?) -> Unit)
    fun updateTask(task: Task, onResult: (Throwable?) -> Unit)
    fun deleteTask(taskId: String, onResult: (Throwable?) -> Unit)
     */
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
    // fun getUsersAsFlow(): Flow<Map<String, User>>
}

class UsernameAlreadyTakenException() : Exception()
class BlankUserOrUsernameException() : Exception()
class FriendshipRequestAlreadySentException() : Exception()
class FriendshipRequestToYourselfException() : Exception()
class FriendshipRequestNotExistingException() : Exception()
class UserNotFoundException() : Exception()
