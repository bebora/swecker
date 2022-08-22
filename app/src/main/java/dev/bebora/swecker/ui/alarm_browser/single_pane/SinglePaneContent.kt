package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bebora.swecker.ui.alarm_browser.*
import dev.bebora.swecker.ui.alarm_browser.alarm_details.AlarmDetails
import dev.bebora.swecker.ui.alarm_browser.chat.ChatScreenContent
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupSinglePaneContent

@Composable
fun AlarmBrowserSinglePaneContent(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState,
) {
    Column(
        modifier = modifier
            .fillMaxSize(1f)
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when (uiState.selectedDestination) {
            NavBarDestination.HOME, NavBarDestination.PERSONAL -> {
                when (uiState.detailsScreenContent) {
                    DetailsScreenContent.ALARM_DETAILS -> {
                        AlarmDetails(
                            alarm = uiState.selectedAlarm!!,
                            isReadOnly = false,
                            onAlarmPartiallyUpdated = { al ->
                                onEvent(
                                    AlarmBrowserEvent.AlarmPartiallyUpdated(
                                        al
                                    )
                                )
                            },
                            onUpdateCompleted = { al, b ->
                                onEvent(
                                    AlarmBrowserEvent.AlarmUpdated(
                                        al,
                                        b
                                    )
                                )
                            }
                        )
                    }
                    DetailsScreenContent.CHAT -> {
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
                    DetailsScreenContent.NONE -> {
                        Column {
                            AlarmList(
                                alarms = uiState.filteredAlarms ?: uiState.alarms,
                                onEvent = onEvent,
                                selectedAlarm = uiState.selectedAlarm
                            )
                            TextButton(onClick = { onEvent(AlarmBrowserEvent.OpenChatTEMP) }) {
                                Text(text = "Open test chat")
                            }
                        }
                    }
                    else -> {}
                }
            }
            NavBarDestination.GROUPS -> {
                GroupSinglePaneContent(onEvent = onEvent, uiState = uiState)
            }
            NavBarDestination.CHANNELS -> {
                Box(modifier = Modifier.fillMaxWidth(1f))
            }
        }
    }
}
