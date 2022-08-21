package dev.bebora.swecker.data.service

import dev.bebora.swecker.data.Message
import kotlinx.coroutines.flow.Flow

interface ChatService {
    fun getMessages(chatId: String): Flow<List<Message>>
    fun sendMessage(chatId: String, senderId: String, text: String, onResult: (Throwable?) -> Unit)
}

class InvalidChatException : Exception()
class InvalidSenderException : Exception()
