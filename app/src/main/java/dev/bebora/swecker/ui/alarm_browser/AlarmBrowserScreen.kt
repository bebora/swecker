package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.alarm_browser.AlarmRepositoryTestImpl
import dev.bebora.swecker.data.local.LocalAlarmDataProvider
import dev.bebora.swecker.ui.add_alarm.AddAlarmDialog
import dev.bebora.swecker.ui.add_alarm.AddAlarmScreen
import dev.bebora.swecker.ui.alarm_browser.alarm_details.AlarmDetails
import dev.bebora.swecker.ui.alarm_browser.chat.ChatScreenPreview
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.util.ADD_CONTACT
import dev.bebora.swecker.util.CONTACT_BROWSER
import dev.bebora.swecker.util.SETTINGS
import kotlinx.coroutines.launch
import java.time.OffsetDateTime


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
                selected =
                    al.id == selectedAlarm.id
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
    selectedGroupId: String? = null,
    onEvent: (AlarmBrowserEvent) -> Unit,
) {
    LazyColumn() {
        items(groups) { group ->
            GroupItem(
                modifier = modifier,
                group = group,
                selected = selectedGroupId == group.id,
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
                        "1",
                        "Wanda the group",
                        members = null,
                        firstAlarmName = "An alarm!",
                        firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
                        owner = "@me"
                    ),
                    Group(
                        "2",
                        "Another group",
                        members = null,
                        firstAlarmName = "An alarm!",
                        firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
                        owner = "@you"
                    ),
                    Group(
                        "3",
                        "A third group! Very long title",
                        members = null,
                        firstAlarmName = "An alarm!",
                        firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
                        owner = "@you"
                    ),
                ),
                selectedGroupId = "3",
                onEvent = {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmBrowserDualPaneContent(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    onOpenDrawer: () -> Unit = {},
    onFabPressed: () -> Unit = {},
    uiState: AlarmBrowserUIState
) {
    Row(
        modifier = modifier
            .fillMaxWidth(1f)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp)
    ) {
        SweckerNavRail(
            alarmBrowserUIState = uiState,
            onEvent = onEvent,
            onOpenDrawer = { onOpenDrawer() },
            onFabPressed = onFabPressed
        )
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
    uiState: AlarmBrowserUIState,
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
    onNavigate: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
// icons to mimic drawer destinations
    val items = remember {
        listOf(
            DrawerSection(
                title = "Alarms management",
                subsections = listOf(
                    DrawerSubSection(
                        title = "Alarms overview",
                        icon = Icons.Outlined.Home,
                        selected = true
                    ) { scope.launch { drawerState.close() } },
                    DrawerSubSection(
                        title = "New group",
                        icon = Icons.Outlined.Groups
                    ) { scope.launch { drawerState.close() } },
                    DrawerSubSection(
                        title = "New channel",
                        icon = Icons.Outlined.Campaign
                    ) { scope.launch { drawerState.close() } },
                )
            ),
            DrawerSection(
                title = "Contacts management",
                subsections = listOf(
                    DrawerSubSection(
                        title = "Contacts",
                        icon = Icons.Outlined.Contacts
                    ) {
                        scope.launch { drawerState.close() }
                        onNavigate(CONTACT_BROWSER)
                      },
                    DrawerSubSection(
                        title = "Friendship requests",
                        icon = Icons.Outlined.GroupAdd
                    ) { scope.launch { drawerState.close() } },
                    DrawerSubSection(
                        title = "Add a friend",
                        icon = Icons.Outlined.PersonAddAlt
                    ) {
                        scope.launch { drawerState.close() }
                        onNavigate(ADD_CONTACT)
                      },
                )
            ),
            DrawerSection(
                title = "Settings",
                subsections = listOf(
                    DrawerSubSection(
                        title = "Settings",
                        icon = Icons.Outlined.Settings
                    ) {
                        scope.launch { drawerState.close() }
                        onNavigate(SETTINGS)
                    },
                )
            )
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Swecker",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 18.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(
                    modifier = Modifier
                        .verticalScroll(state = rememberScrollState()),
                ) {
                    items.forEachIndexed { idx, drawerSection ->
                        if (idx != 0) {
                            Divider()
                        }
                        Text(
                            text = drawerSection.title,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 24.dp,
                                top = 18.dp,
                                bottom = 18.dp
                            ),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        drawerSection.subsections.forEach { subsection ->
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        subsection.icon,
                                        contentDescription = subsection.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = subsection.title,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                },
                                selected = subsection.selected,
                                onClick = {
                                    scope.launch { subsection.onClick() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            }
        },
        content = {
            BoxWithConstraints() {
                if (maxWidth < 840.dp) {
                    if (uiState.showAddAlarm) {
                        AddAlarmScreen(modifier = Modifier.fillMaxSize(1f),
                            onGoBack = { viewModel.onEvent(AlarmBrowserEvent.ToggleAddAlarm) })
                    } else {
                        Scaffold(
                            topBar = {
                                SweckerTopAppBar(
                                    modifier = modifier,
                                    uiState = uiState,
                                    onEvent = viewModel::onEvent,
                                    colors = TopAppBarDefaults.smallTopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    onOpenDrawer = { scope.launch { drawerState.open() } }
                                )
                            },
                            floatingActionButton = {
                                SweckerFab(
                                    destination = uiState.selectedDestination,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(32.dp)
                                ) {
                                    when (uiState.selectedDestination) {
                                        NavBarDestination.PERSONAL, NavBarDestination.HOME -> {
                                            viewModel.onEvent(AlarmBrowserEvent.ToggleAddAlarm)
                                        }
                                        else -> {}
                                    }
                                }
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
                                uiState = uiState,
                            )

                        }
                    }
                } else {
                    AlarmBrowserDualPaneContent(
                        onEvent = viewModel::onEvent,
                        uiState = uiState,
                        onOpenDrawer = {
                            scope.launch { drawerState.open() }
                        },
                        onFabPressed = {
                            when (uiState.selectedDestination) {
                                NavBarDestination.PERSONAL, NavBarDestination.HOME -> {
                                    viewModel.onEvent(AlarmBrowserEvent.ToggleAddAlarm)
                                }
                                else -> {}
                            }
                            if (uiState.openContent == DetailsScreenContent.GROUP_ALARM_LIST) {
                                viewModel.onEvent(AlarmBrowserEvent.ToggleAddAlarm)
                            }
                        }
                    )
                    if (uiState.showAddAlarm) {
                        AddAlarmDialog(
                            onGoBack = { viewModel.onEvent(AlarmBrowserEvent.ToggleAddAlarm) })
                    }
                }
            }
        })
}

@Preview(showBackground = true)
@Composable
fun AlarmBrowserScreenPreview() {
    val testViewModel = AlarmBrowserViewModel(AlarmRepositoryTestImpl())
    AlarmBrowserScreen(viewModel = testViewModel)
}
