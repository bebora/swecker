package dev.bebora.swecker.data.service

import dev.bebora.swecker.data.User

interface StorageService {
    fun getUser(
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (User) -> Unit
    )
    // TODO handle profile picture

    fun addUserListener()
    fun saveUser(requestedUser: User, onResult: (Throwable?) -> Unit)
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
}

class UsernameAlreadyTakenException() : Exception()
class BlankUserOrUsername() : Exception()
