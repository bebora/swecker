package dev.bebora.swecker.ui.alarm_browser.group_screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bebora.swecker.ui.alarm_browser.*
import dev.bebora.swecker.ui.alarm_browser.alarm_details.AlarmDetails
import dev.bebora.swecker.ui.alarm_browser.chat.ChatScreenPreview

@Composable
fun GroupSinglePaneContent(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState
) {
    when (uiState.detailsScreenContent) {
        DetailsScreenContent.ALARM_DETAILS -> {
            AlarmDetails(alarm = uiState.selectedAlarm!!,
                isReadOnly = false,
                onAlarmPartiallyUpdated = { al -> onEvent(AlarmBrowserEvent.AlarmPartiallyUpdated(al)) },
                onUpdateCompleted = { al, b -> onEvent(AlarmBrowserEvent.AlarmUpdated(al, b)) })
        }
        DetailsScreenContent.GROUP_ALARM_LIST -> {
            AlarmList(alarms = uiState.filteredAlarms!!, modifier = modifier, onEvent = onEvent)
        }
        DetailsScreenContent.CHAT -> {
            ChatScreenPreview()
        }

        DetailsScreenContent.GROUP_DETAILS -> {
            GroupDetails(group = uiState.selectedGroup!!)
        }

        DetailsScreenContent.NONE -> {
            GroupList(
                groups = uiState.groups,
                onEvent = onEvent,
                selectedGroupId = uiState.selectedGroup?.id
            )
        }
    }
}
