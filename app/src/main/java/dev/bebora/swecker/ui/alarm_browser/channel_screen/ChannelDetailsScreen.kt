package dev.bebora.swecker.ui.alarm_browser.channel_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.alarm_browser.DetailsScreenContent
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetailsScreen(
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
                ChannelTopAppBar(
                    channel = uiState.selectedChannel!!,
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.clickable{onEvent(AlarmBrowserEvent.DetailsOpened(type = DetailsScreenContent.CHANNEL_DETAILS))},
                    onGoBack = {onEvent(AlarmBrowserEvent.BackButtonPressed)}
                )
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            GroupDetails(
                modifier = modifier,group = uiState.selectedChannel!!, usersData = uiState.usersData)
        }
    }
}
