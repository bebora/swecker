package dev.bebora.swecker.ui.alarm_browser.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.theme.SweckerTheme

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    body: String = "",
    profilePic: ImageBitmap? = null,
    author: String,
    showContactName: Boolean,
    isLastMessage: Boolean = false,
    isOwnMessage: Boolean
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
            Image(
                painter = if (profilePic != null) {
                    BitmapPainter(image = profilePic)
                } else {
                    painterResource(dev.bebora.swecker.R.drawable.temp_icon)
                },
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
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
                        text = author,
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
                    style = MaterialTheme.typography.bodyMedium
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
