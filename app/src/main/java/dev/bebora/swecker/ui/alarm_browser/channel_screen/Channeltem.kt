package dev.bebora.swecker.ui.alarm_browser.channel_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.bebora.swecker.data.Group
import java.time.format.DateTimeFormatter

@Composable
fun ChannelItem(
    modifier: Modifier = Modifier,
    channel: Group,
    selected: Boolean = false,
    canJoin: Boolean = false,
    onEvent: (Group) -> Unit = {},
    onChannelJoin: () -> Unit = {}
) {
    Row(modifier = modifier
        .fillMaxWidth(1f)
        .background(
            if (!selected) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            shape = ShapeDefaults.Large
        )
        .clickable { onEvent(channel) }
        .padding(all = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = channel.groupPicUrl,
            contentDescription = "Group profile picture",
            placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
            error = ColorPainter(MaterialTheme.colorScheme.tertiary),
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = channel.name,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.headlineMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.weight(1f))

                if(canJoin) {
                    FilledTonalIconButton(modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = onChannelJoin) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Join group"
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = channel.firstAlarmName,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .widthIn(0.dp, 120.dp)
                        .padding(all = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = channel.firstAlarmDateTime?.format(DateTimeFormatter.ofPattern("eee\n dd MMM uuuu")) ?: "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .width(100.dp),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                )
            }
        }
    }
}
