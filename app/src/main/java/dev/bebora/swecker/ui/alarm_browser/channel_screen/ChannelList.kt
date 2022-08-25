package dev.bebora.swecker.ui.alarm_browser.channel_screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bebora.swecker.data.Group

@Composable
fun ChannelList(
    modifier: Modifier = Modifier,
    channels: List<Group>,
    myId: String,
    selectedChannelId: String? = null,
    onEvent: (Group) -> Unit = {},
    onChannelJoin: (Group) -> Unit = {}
) {
    LazyColumn() {
        items(channels) { channel ->
            val canJoin = !channel.members.contains(myId)
            ChannelItem(
                modifier = modifier,
                channel = channel,
                selected = selectedChannelId == channel.id,
                onEvent = onEvent,
                canJoin = canJoin,
                onChannelJoin = if (canJoin) {
                    {
                        onChannelJoin(channel)
                    }
                } else {
                    {}
                }
            )
        }
    }
}
