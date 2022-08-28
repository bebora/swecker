package dev.bebora.swecker.data.service.testimpl

import dev.bebora.swecker.data.Message
import dev.bebora.swecker.data.service.ChatService
import dev.bebora.swecker.data.service.InvalidChatException
import dev.bebora.swecker.data.service.InvalidSenderException
import kotlinx.coroutines.flow.*

class FakeChatService : ChatService {
    private val messagesMap: MutableMap<String, List<Message>> = mutableMapOf()
    private val chatUpdates = MutableStateFlow(0)

    override fun getMessages(chatId: String): Flow<List<Message>> {
        return chatUpdates.asStateFlow().map {
            messagesMap[chatId] ?: emptyList()
        }
    }

    override fun sendMessage(
        chatId: String,
        senderId: String,
        text: String,
        onResult: (Throwable?) -> Unit
    ) {
        if (chatId.isBlank()) {
            onResult(InvalidChatException())
        } else if (senderId.isBlank()) {
            onResult(InvalidSenderException())
        }
        else {
            val oldMessages = messagesMap[chatId] ?: emptyList()
            messagesMap[chatId] = oldMessages + listOf(Message(
                uId = senderId,
                text = text,
                time = System.currentTimeMillis()
            ))
            chatUpdates.value += 1
            onResult(null)
        }
    }
}
