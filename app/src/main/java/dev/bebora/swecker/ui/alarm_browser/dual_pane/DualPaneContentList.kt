package dev.bebora.swecker.ui.alarm_browser.dual_pane

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.alarm_browser.*
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupList

@Composable
fun DualPaneContentList(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState
) {
    Column(
        modifier = modifier
            .widthIn(200.dp, 350.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AlarmBrowserSearchBar(searchKey = uiState.searchKey, modifier = modifier, onEvent = onEvent)
        when (uiState.selectedDestination) {
            NavBarDestination.HOME, NavBarDestination.PERSONAL -> {
                AlarmList(
                    modifier = Modifier.widthIn(200.dp, 350.dp),
                    alarms = uiState.filteredAlarms ?: uiState.alarms,
                    onEvent = onEvent,
                    selectedAlarm = uiState.selectedAlarm
                )
            }
            NavBarDestination.GROUPS -> {
                GroupList(groups = uiState.groups, onEvent = onEvent)
            }
            NavBarDestination.CHANNELS -> {
                Box(modifier = Modifier.fillMaxWidth(1f))
            }
        }
    }
}
