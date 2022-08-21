package dev.bebora.swecker.data.service.impl

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.bebora.swecker.data.Message
import dev.bebora.swecker.data.service.ChatService
import dev.bebora.swecker.data.service.InvalidChatException
import dev.bebora.swecker.data.service.InvalidSenderException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow

class ChatServiceImpl : ChatService {
    override fun getMessages(chatId: String): Flow<List<Message>> {
        if (chatId.isBlank()) {
            return emptyFlow()
        } else {
            return callbackFlow {
                val listener = Firebase.firestore
                    .collection(FirebaseConstants.CHATS)
                    .document(chatId)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w("SWECKER-GET-CHAT", "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            Log.d("SWECKER-GET-CHAT-EXISTS", "Current data: ${snapshot.data}")
                            trySend(
                                snapshot.toObject(ChatDocument::class.java)?.messages
                                    ?: emptyList()
                            )
                        } else {
                            Log.d("SWECKER-GET-CHAT-NOPE", "Current data: null")
                            trySend(emptyList())
                        }
                    }
                awaitClose {
                    listener.remove()
                }
            }
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
        }
        else if (senderId.isBlank()) {
            onResult(InvalidSenderException())
        }
        else {
            Log.d("SWECKER-SEND-MSG-FIRE", "Sender is is '$senderId'")
            Log.d("SWECKER-SEND-MSG-BUG", "Coso + '${FieldValue.arrayUnion(
                Message(
                    text = text,
                    time = System.currentTimeMillis(),
                    uId = senderId,
                ))}'")

            Firebase.firestore
                .collection(FirebaseConstants.CHATS)
                .document(chatId)
                .update("messages", FieldValue.arrayUnion(
                    Message(
                        text = text,
                        time = System.currentTimeMillis(),
                        uId = senderId,
                    )))
                .addOnCompleteListener {
                    onResult(it.exception)
                }
        }
    }
}

data class ChatDocument(
    val id: String = "",
    val messages: List<Message> = emptyList()
)
