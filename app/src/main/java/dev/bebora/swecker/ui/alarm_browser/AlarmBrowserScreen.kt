package dev.bebora.swecker.ui.alarm_browser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
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
import dev.bebora.swecker.data.alarm_browser.AlarmRepositoryTestImpl
import dev.bebora.swecker.data.service.impl.AccountsServiceImpl
import dev.bebora.swecker.data.service.impl.AuthServiceImpl
import dev.bebora.swecker.data.service.impl.ChatServiceImpl
import dev.bebora.swecker.ui.alarm_browser.dual_pane.AlarmBrowserDualPaneContent
import dev.bebora.swecker.ui.alarm_browser.dual_pane.DualPaneDialog
import dev.bebora.swecker.ui.alarm_browser.single_pane.AlarmBrowserNavBar
import dev.bebora.swecker.ui.alarm_browser.single_pane.AlarmBrowserSinglePaneContent
import dev.bebora.swecker.ui.alarm_browser.single_pane.AlarmBrowserSinglePaneFab
import dev.bebora.swecker.ui.alarm_browser.single_pane.SinglePaneDialog
import dev.bebora.swecker.util.ADD_CONTACT
import dev.bebora.swecker.util.SETTINGS
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmBrowserScreen(
    modifier: Modifier = Modifier,
    viewModel: AlarmBrowserViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit = {},
) {
    val uiState = viewModel.uiState
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
                    ) {
                        scope.launch { drawerState.close() }
                        viewModel.onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_GROUP))
                    },
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
                        viewModel.onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.CONTACT_BROWSER))
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
                    if (uiState.dialogContent != DialogContent.NONE) {
                        BackHandler {
                            viewModel.onEvent(AlarmBrowserEvent.BackButtonPressed)
                        }
                        SinglePaneDialog(
                            dialogContent = uiState.dialogContent,
                            onNavigate = onNavigate,
                            group = uiState.selectedGroup,
                            onEvent = viewModel::onEvent
                        )
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
                                AlarmBrowserSinglePaneFab(
                                    destination = uiState.selectedDestination,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(32.dp),
                                    detailsScreenContent = uiState.detailsScreenContent,
                                    onEvent = viewModel::onEvent
                                )
                            },
                            bottomBar = {
                                AlarmBrowserNavBar(
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
                    BackHandler() {
                        viewModel.onEvent(AlarmBrowserEvent.BackButtonPressed)
                    }
                    AlarmBrowserDualPaneContent(
                        onEvent = viewModel::onEvent,
                        uiState = uiState,
                        onOpenDrawer = {
                            scope.launch { drawerState.open() }
                        },
                    )
                    if (uiState.dialogContent != DialogContent.NONE) {
                        DualPaneDialog(
                            dialogContent = uiState.dialogContent,
                            onNavigate = onNavigate,
                            onEvent = viewModel::onEvent
                        )
                    }
                }
            }
        })
}

@Preview(showBackground = true)
@Composable
fun AlarmBrowserScreenPreview() {
    val testViewModel = AlarmBrowserViewModel(
        AlarmRepositoryTestImpl(),
        chatService = ChatServiceImpl(),
        accountsService = AccountsServiceImpl(),
        authService = AuthServiceImpl()
    )
    AlarmBrowserScreen(viewModel = testViewModel)
}
