package dev.bebora.swecker.ui.alarm_browser.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Message
import dev.bebora.swecker.data.User
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.theme.SweckerTheme
import java.lang.System.currentTimeMillis
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun ChatScreenContent(
    modifier: Modifier,
    messages: List<Message>,
    usersData: Map<String, User>,
    ownerId: String,
    onSendMessage: (String) -> Unit = {}
) {
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = 0
    )
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
            reverseLayout = true
        ) {
            itemsIndexed(
                items = messages,
                key = { idx, _ -> idx }) { index: Int, message ->
                val isOwnMessage = message.uId == ownerId
                val isFirstMessage =
                    (index + 1 > messages.lastIndex) || message.uId != messages[index + 1].uId

                val isLastMessage =
                    (index - 1 < 0) || message.uId != messages[index - 1].uId

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState,
    roundTopCorners: Boolean
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Surface(
                shape = if (roundTopCorners) {
                    RoundedCornerShape(
                        topEnd = 20.dp,
                        topStart = 20.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                } else {
                    RectangleShape
                }
            ) {
                ChatTopAppBar(
                    modifier = modifier,
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    title = uiState.selectedAlarm?.name ?: "Hello world",
                    date = (uiState.selectedAlarm?.localDate ?: LocalDate.now()).format(
                        DateTimeFormatter.ofPattern("eee, dd MMM uuuu")
                    )
                        ?: "",
                    onEvent = onEvent,
                )
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            ChatScreenContent(
                modifier = Modifier,
                messages = uiState.messages,
                ownerId = uiState.me.id,
                usersData = uiState.usersData,
                onSendMessage = {
                    onEvent(AlarmBrowserEvent.SendMessageTEMP(it))
                }
            )
        }
    }
}
