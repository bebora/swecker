package dev.bebora.swecker.ui.alarm_browser.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Message
import dev.bebora.swecker.data.User
import dev.bebora.swecker.ui.theme.SweckerTheme
import java.lang.System.currentTimeMillis


@Composable
fun ChatScreenContent(
    modifier: Modifier,
    messages: List<Message>,
    usersData: Map<String, User>,
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
                key = { idx, _ -> idx }) { index: Int, message ->
                val isOwnMessage = message.uId == ownerId
                val isFirstMessage =
                    (index - 1 < 0) || message.uId != messages[index - 1].uId
                val isLastMessage =
                    (index + 1 > messages.lastIndex) || message.uId != messages[index + 1].uId

                //TODO get actual contact name and image
                MessageItem(
                    author = usersData[message.uId]?.name,
                    body = message.text,
                    showContactName = isFirstMessage && !isOwnMessage,
                    isOwnMessage = isOwnMessage,
                    isLastMessage = isLastMessage,
                    propicUrl = usersData[message.uId]?.propicUrl
                )
            }
        }

        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                lazyListState.animateScrollToItem(messages.size - 1)
            }
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
                        time = 1661089449,
                        uId = "@me",
                        text = "Prova di un mio messaggio"
                    ),
                    Message(
                        time = 1661089459,
                        uId = "@me",
                        text = "Prova di un altro mio messaggio"
                    ),
                    Message(
                        time = 1661089549,
                        uId = "@you",
                        text = "Hi, this is a super damn " +
                                "incredibly long multilined message!" +
                                "Incredible! Let's see how stuff behaves with very long messages" +
                                " such as this incredibly long message" +
                                "incredibly long multilined message!" +
                                "Incredible! Let's see how stuff behaves with very long messages" +
                                " such as this incredibly long message"
                    ),
                    Message(
                        time = 1661089699,
                        uId = "@you",
                        text = "Message from my friend"
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
                            time = currentTimeMillis(),
                            text = newMessage,
                            uId = "@me"
                        )
                    )
                },
                usersData = emptyMap()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ChatScreenDynamicPreview() {
    SweckerTheme() {
        Scaffold() {
            ChatScreenContent(
                modifier = Modifier.padding(it),
                messages = emptyList(),
                ownerId = "hello",
                usersData = emptyMap()
            )
        }
    }
}
