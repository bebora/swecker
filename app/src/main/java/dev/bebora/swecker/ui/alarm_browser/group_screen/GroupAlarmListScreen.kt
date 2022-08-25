package dev.bebora.swecker.ui.alarm_browser.group_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.alarm_browser.*
import dev.bebora.swecker.ui.alarm_browser.single_pane.AlarmBrowserSinglePaneFab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupAlarmListScreen(
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
                GroupTopAppBar(
                    group = uiState.selectedGroup!!,
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.clickable{onEvent(AlarmBrowserEvent.DetailsOpened(type = DetailsScreenContent.GROUP_DETAILS))},
                    onGoBack = {onEvent(AlarmBrowserEvent.BackButtonPressed)}
                )
            }
        },
        floatingActionButton = {
            AlarmBrowserSinglePaneFab(
                destination = NavBarDestination.GROUPS,
                modifier = Modifier
                    .padding(32.dp),
                detailsScreenContent = uiState.detailsScreenContent,
                onEvent = onEvent
            )
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            AlarmList(
                alarms = uiState.filteredAlarms!!,
                onEvent = onEvent,
                selectedAlarm = uiState.selectedAlarm
            )
        }
    }
}
