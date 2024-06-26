package dev.bebora.swecker.ui.alarm_browser.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.DetailsScreenContent
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.util.TestConstants

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    body: String = "",
    propicUrl: String? = null,
    author: String?, // if null, show a loading animation or a default name
    showContactName: Boolean,
    isLastMessage: Boolean = false,
    isOwnMessage: Boolean,
    time: Long? = 0
) {
    Row(
        modifier = modifier
            .fillMaxWidth(1f)
            .padding(horizontal = 8.dp),
        horizontalArrangement = if (isOwnMessage) {
            Arrangement.End
        } else {
            Arrangement.Start
        },
        verticalAlignment = Alignment.Bottom
    ) {

        if (isLastMessage && !isOwnMessage) {
            AsyncImage(
                model = propicUrl,
                contentDescription = "Profile picture",
                placeholder = painterResource(R.drawable.temp_icon),
                error = painterResource(R.drawable.temp_icon),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        } else if (!isOwnMessage) {
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            modifier = if (!isOwnMessage) {
                Modifier.padding(PaddingValues(0.dp, 0.dp, 40.dp, 0.dp))
            } else {
                Modifier.padding(PaddingValues(40.dp, 0.dp, 0.dp, 0.dp))
            },
            shape = if (isLastMessage) {
                if (isOwnMessage) {
                    RoundedCornerShape(
                        topEnd = CornerSize(10.dp),
                        topStart = CornerSize(10.dp),
                        bottomEnd = CornerSize(0.dp),
                        bottomStart = (CornerSize(10.dp))
                    )
                } else {
                    RoundedCornerShape(
                        topEnd = CornerSize(10.dp),
                        topStart = CornerSize(10.dp),
                        bottomEnd = CornerSize(10.dp),
                        bottomStart = (CornerSize(0.dp))
                    )
                }
            } else {
                ShapeDefaults.Medium
            },
            color = if (isOwnMessage) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            }
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                if (showContactName) {
                    Text(
                        text = author ?: stringResource(id = R.string.loading), //TODO add animation instead of fixed name?
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = body,
                    modifier = Modifier.padding(all = 4.dp),
                    color = if (isOwnMessage) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onTertiaryContainer
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MessageItemPreview() {
    SweckerTheme() {
        Scaffold() {
            Column(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(it),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                MessageItem(
                    author = "Fabio",
                    body = "Così vanno bene i messaggi?",
                    isLastMessage = false,
                    showContactName = true,
                    isOwnMessage = false
                )
                MessageItem(
                    author = "Fabio",
                    body = "Nome all'inizio",
                    isLastMessage = false,
                    showContactName = false,
                    isOwnMessage = false
                )
                MessageItem(
                    author = "Fabio",
                    body = "Pic in fondo",
                    isLastMessage = true,
                    showContactName = false,
                    isOwnMessage = false
                )
                MessageItem(
                    author = "Simone",
                    body = "Perfetto",
                    isLastMessage = false,
                    showContactName = false,
                    isOwnMessage = true
                )
                MessageItem(
                    author = "Simone",
                    body = "Prova del compotarmento di un messaggio lungo," +
                            " ma veramente lunghissimo",
                    isLastMessage = true,
                    showContactName = false,
                    isOwnMessage = true
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputBar(
    modifier: Modifier = Modifier,
    messageBody: String,
    onSendMessage: (String) -> Unit = {},
    onMessageValueChange: (String) -> Unit = {}
) {

    Surface(
        modifier = modifier
            .defaultMinSize(55.dp)
            .fillMaxWidth(1f),
        shape = ShapeDefaults.ExtraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                modifier = Modifier.weight(1f),
                value = messageBody,
                onValueChange = { onMessageValueChange(it) },
                placeholder = { Text(stringResource(R.string.message_placeholder)) },
                readOnly = false,
                maxLines = 10,
            )
            if (messageBody.isNotEmpty()) {
                IconButton(modifier = Modifier
                    .testTag(TestConstants.sendMessage)
                    .padding(vertical = 4.dp),
                    onClick = {
                        onSendMessage(messageBody)
                        onMessageValueChange("")
                    }) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,

                        )
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MessageInputBarPreview() {
    SweckerTheme() {
        Scaffold() {
            Column(
                modifier = Modifier
                    .fillMaxSize(1f)
                    .padding(it),
                verticalArrangement = Arrangement.Bottom
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                MessageInputBar(
                    modifier = Modifier, messageBody = "Hi, this is a super damn " +
                            "incredibly long multilined message!" +
                            "Incredible! Let's see how stuff behaves with very long messages" +
                            " such as this incredibly long message" +
                            "incredibly long multilined message!" +
                            "Incredible! Let's see how stuff behaves with very long messages" +
                            " such as this incredibly long message"
                )
                Spacer(modifier = Modifier.height(4.dp))
                MessageInputBar(
                    modifier = Modifier, messageBody = "Short stuff"
                )
                Spacer(modifier = Modifier.height(4.dp))
                MessageInputBar(
                    modifier = Modifier, messageBody = ""
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopAppBar(
    modifier: Modifier,
    colors: TopAppBarColors,
    title: String,
    date: String,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    SmallTopAppBar(
        colors = colors,
        modifier = modifier.clickable {
            onEvent(AlarmBrowserEvent.DetailsOpened(DetailsScreenContent.ALARM_DETAILS))
        },
        title = {
            Column() {
                Text(
                    modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp),
                    text = title,
                    textAlign = TextAlign.Left,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = true
                )
                Text(
                    text = date,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { onEvent(AlarmBrowserEvent.BackButtonPressed) }) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        actions = {
        })
}
