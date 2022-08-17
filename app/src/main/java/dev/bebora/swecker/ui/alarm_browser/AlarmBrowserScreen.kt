package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmRepositoryImpl
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.local.LocalAlarmDataProvider
import dev.bebora.swecker.ui.alarm_browser.alarm_details.AlarmDetails
import dev.bebora.swecker.ui.alarm_browser.chat.ChatScreenPreview
import dev.bebora.swecker.ui.theme.SweckerTheme


@Composable
fun AlarmList(
    alarms: List<Alarm>,
    modifier: Modifier = Modifier,
    selectedAlarm: Alarm? = null,
    onEvent: (AlarmBrowserEvent) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.widthIn(200.dp, 500.dp)
    ) {
        items(items = alarms, key = { al -> al.id }) { al ->
            var selected = false
            if (selectedAlarm != null) {
                selected = al.id == selectedAlarm.id
            }
            AlarmCard(alarm = al, modifier = modifier, onEvent = onEvent, selected = selected)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmListPreview() {
    SweckerTheme() {
        AlarmList(alarms = LocalAlarmDataProvider.allAlarms, onEvent = { })
    }
}

@Composable
fun GroupList(
    modifier: Modifier = Modifier,
    groups: List<Group>,
    selectedGroupId: Long? = null,
    onEvent: (AlarmBrowserEvent) -> Unit,
) {
    LazyColumn() {
        items(groups) { group ->
            GroupItem(
                modifier = modifier,
                group = group,
                selected = selectedGroupId == group.id,
                firstAlarm = group.alarms.first(),
                onEvent = onEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GroupListPreview() {
    SweckerTheme() {
        Scaffold() {
            GroupList(
                modifier = Modifier.padding(it),
                groups = listOf(
                    Group(
                        1,
                        "Wanda the group",
                        members = null,
                        alarms = listOf(
                            Alarm(
                                id = "@monesi#1",
                                name = "Alarm test",
                                time = "14:30",
                                date = "mon 7 December",
                                alarmType = AlarmType.PERSONAL
                            )
                        ),
                        owner = "@me"
                    ),
                    Group(
                        2,
                        "Another group",
                        members = null,
                        alarms = listOf(
                            Alarm(
                                id = "@monesi#1",
                                name = "Alarm test",
                                time = "14:30",
                                date = "mon 7 December",
                                alarmType = AlarmType.PERSONAL
                            )
                        ),
                        owner = "@you"
                    ),
                    Group(
                        3,
                        "A third group! Very long title",
                        members = null,
                        alarms = listOf(
                            Alarm(
                                id = "@monesi#1",
                                name = "Alarm test",
                                time = "14:30",
                                date = "mon 7 December",
                                alarmType = AlarmType.PERSONAL
                            )
                        ),
                        owner = "@you"
                    ),
                ),
                selectedGroupId = 3,
                onEvent = {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmBrowserDualPaneContent(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState
) {
    Row(
        modifier = modifier
            .fillMaxWidth(1f)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp)
    ) {
        SweckerNavRail(alarmBrowserUIState = uiState, onEvent = onEvent)
        DualPaneContentList(onEvent = onEvent, uiState = uiState)
        Scaffold(
            topBar = {
                SweckerDetailsAppBar(
                    uiState = uiState,
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    onEvent = onEvent,
                    roundTopCorners = true
                )
            }

        ) {
            DualPaneContentDetails(
                modifier = modifier.padding(it),
                onEvent = onEvent,
                uiState = uiState
            )
        }
    }
}

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
        when (uiState.openContent) {
            DetailsScreenContent.ALARM_DETAILS -> {
                AlarmDetails(
                    alarm = uiState.selectedAlarm!!,
                    isReadOnly = false,
                    onEvent = onEvent
                )
            }
            DetailsScreenContent.GROUP_ALARM_LIST -> {
                AlarmList(
                    alarms = uiState.filteredAlarms!!,
                    onEvent = onEvent,
                    selectedAlarm = uiState.selectedAlarm
                )
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
fun AlarmBrowserSinglePaneContent(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState
) {
    Box(
        modifier = modifier
            .fillMaxSize(1f)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.TopCenter
    ) {
        when (uiState.selectedDestination) {
            NavBarDestination.HOME, NavBarDestination.PERSONAL -> {
                when (uiState.openContent) {
                    DetailsScreenContent.ALARM_DETAILS -> {
                        AlarmDetails(
                            alarm = uiState.selectedAlarm!!,
                            isReadOnly = false, onEvent = onEvent
                        )
                    }
                    DetailsScreenContent.CHAT -> {
                        ChatScreenPreview()
                    }
                    DetailsScreenContent.NONE -> {
                        AlarmList(
                            alarms = uiState.filteredAlarms ?: uiState.alarms,
                            onEvent = onEvent,
                            selectedAlarm = uiState.selectedAlarm
                        )
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

@Composable
fun GroupSinglePaneContent(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState
) {
    when (uiState.openContent) {
        DetailsScreenContent.ALARM_DETAILS -> {
            AlarmDetails(alarm = uiState.selectedAlarm!!, isReadOnly = false, onEvent = onEvent)
        }
        DetailsScreenContent.GROUP_ALARM_LIST -> {
            AlarmList(alarms = uiState.filteredAlarms!!, modifier = modifier, onEvent = onEvent)
        }
        DetailsScreenContent.CHAT -> {
            ChatScreenPreview()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmBrowserScreen(
    modifier: Modifier = Modifier,
    viewModel: AlarmBrowserViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    SweckerTheme() {
        BoxWithConstraints() {
            if (maxWidth < 840.dp) {
                Scaffold(
                    topBar = {
                        SweckerTopAppBar(
                            modifier = modifier,
                            uiState = uiState,
                            onEvent = viewModel::onEvent,
                            colors = TopAppBarDefaults.smallTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    },
                    bottomBar = {
                        SweckerNavBar(
                            alarmBrowserUIState = uiState,
                            onEvent = viewModel::onEvent,
                        )
                    }) {
                    AlarmBrowserSinglePaneContent(
                        modifier = Modifier.padding(it),
                        onEvent = viewModel::onEvent,
                        uiState = uiState
                    )
                }
            } else {
                AlarmBrowserDualPaneContent(onEvent = viewModel::onEvent, uiState = uiState)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmBrowserScreenPreview() {
    val testViewModel = AlarmBrowserViewModel(AlarmRepositoryImpl())
    AlarmBrowserScreen(viewModel = testViewModel)
}
