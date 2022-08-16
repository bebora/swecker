package dev.bebora.swecker.ui.alarm_browser.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.theme.SweckerTheme

//TODO add actual message data class
data class Message(
    val messageId: String, val senderId: String, val messageBody: String
)

@Composable
fun ChatScreenContent(
    modifier: Modifier,
    messages: List<Message>,
    ownerId: String,
    onSendMessage: (String) -> Unit = {}
) {
    val lazyListState = rememberLazyListState()
    Column(
        modifier = modifier
            .fillMaxSize(1f)
            .padding(4.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(
            modifier =
            Modifier
                .weight(1f),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(
                items = messages,
                key = { _, message -> message.messageId }) { index: Int, message ->
                val isOwnMessage = message.senderId == ownerId
                val isFirstMessage =
                    (index - 1 < 0) || message.senderId != messages[index - 1].senderId
                val isLastMessage =
                    (index + 1 > messages.lastIndex) || message.senderId != messages[index + 1].senderId

                //TODO get actual contact name and image
                MessageItem(
                    author = message.senderId,
                    body = message.messageBody,
                    showContactName = isFirstMessage && !isOwnMessage,
                    isOwnMessage = isOwnMessage,
                    isLastMessage = isLastMessage,
                )
            }

        }

        LaunchedEffect(messages.size) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }

        Spacer(modifier = Modifier.height(4.dp))
        MessageInputBar(messageBody = "", onSendMessage = onSendMessage)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    SweckerTheme() {
        Scaffold() {
            val messages = remember {
                mutableStateListOf(
                    Message(
                        messageId = "@150109",
                        senderId = "@me",
                        messageBody = "Prova di un mio messaggio"
                    ),
                    Message(
                        messageId = "@12345665",
                        senderId = "@me",
                        messageBody = "Prova di un altro mio messaggio"
                    ),
                    Message(
                        messageId = "@451451",
                        senderId = "@you",
                        messageBody = "Hi, this is a super damn " +
                                "incredibly long multilined message!" +
                                "Incredible! Let's see how stuff behaves with very long messages" +
                                " such as this incredibly long message" +
                                "incredibly long multilined message!" +
                                "Incredible! Let's see how stuff behaves with very long messages" +
                                " such as this incredibly long message"
                    ),
                    Message(
                        messageId = "@1asdf",
                        senderId = "@you",
                        messageBody = "Message from my friend"
                    ),
                )
            }
            var i = 0

            ChatScreenContent(
                modifier = Modifier.padding(it),
                messages = messages,
                ownerId = "@me",
                onSendMessage = { newMessage ->
                    i++
                    messages.add(
                        Message(
                            messageId = "@123f$i",
                            messageBody = newMessage,
                            senderId = "@me"
                        )
                    )
                }
            )
        }
    }
}
