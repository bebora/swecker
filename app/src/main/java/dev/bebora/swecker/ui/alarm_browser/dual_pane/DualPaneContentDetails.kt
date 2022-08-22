package dev.bebora.swecker.ui.alarm_browser.dual_pane

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAlarm
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bebora.swecker.ui.alarm_browser.*
import dev.bebora.swecker.ui.alarm_browser.alarm_details.AlarmDetails
import dev.bebora.swecker.ui.alarm_browser.chat.ChatScreenPreview
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupDetails

@Composable
fun DualPaneContentDetails(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState
) {
    Column(
        modifier = modifier.fillMaxWidth(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                    onUpdateCompleted = { al, b -> onEvent(AlarmBrowserEvent.AlarmUpdated(al, b)) }
                )
            }
            DetailsScreenContent.GROUP_ALARM_LIST -> {
                AlarmList(
                    alarms = uiState.filteredAlarms!!,
                    onEvent = onEvent,
                    selectedAlarm = uiState.selectedAlarm
                )
            }
            DetailsScreenContent.GROUP_DETAILS -> {
                GroupDetails(group = uiState.selectedGroup!!)
            }
            DetailsScreenContent.CHAT -> {
                ChatScreenPreview()
            }
            DetailsScreenContent.NONE -> {

                Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Select something",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DetailsScreenFab(
    modifier: Modifier = Modifier,
    detailsScreenContent: DetailsScreenContent,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    when (detailsScreenContent) {
        DetailsScreenContent.GROUP_ALARM_LIST -> {
            FloatingActionButton(
                modifier = modifier,
                onClick = {
                    onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_ALARM))
                }) {
                Icon(
                    imageVector = Icons.Outlined.AddAlarm,
                    contentDescription = null
                )
            }
        }
        else -> {
        }
    }
}
