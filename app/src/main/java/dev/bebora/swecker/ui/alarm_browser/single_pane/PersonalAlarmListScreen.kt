package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.alarm_browser.AlarmList
import dev.bebora.swecker.ui.alarm_browser.NavBarDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalAlarmListScreen(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState,
    navigationAction: () -> Unit,
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = modifier,
                title = { Text(text = "Personal", textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = { navigationAction() }) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Open hamburger menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search content"
                        )
                    }
                })
        },
        floatingActionButton = {
            AlarmBrowserSinglePaneFab(
                destination = NavBarDestination.PERSONAL,
                modifier = Modifier
                    .padding(32.dp),
                detailsScreenContent = uiState.detailsScreenContent,
                onEvent = onEvent
            )
        },
        bottomBar = {
            AlarmBrowserNavBar(
                alarmBrowserUIState = uiState,
                onEvent = onEvent,
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
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
}
