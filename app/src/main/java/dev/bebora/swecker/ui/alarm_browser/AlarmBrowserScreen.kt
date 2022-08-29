package dev.bebora.swecker.ui.alarm_browser

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.alarm_browser.PreviewAlarmRepository
import dev.bebora.swecker.data.service.impl.AlarmProviderServiceImpl
import dev.bebora.swecker.data.service.impl.ChatServiceImpl
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.ui.alarm_browser.dual_pane.AlarmBrowserDualPaneContent
import dev.bebora.swecker.ui.alarm_browser.dual_pane.DualPaneDialog
import dev.bebora.swecker.ui.alarm_browser.single_pane.*
import dev.bebora.swecker.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AlarmBrowserScreen(
    modifier: Modifier = Modifier,
    viewModel: AlarmBrowserViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit = {},
) {
    val uiState = viewModel.uiState
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val newGroup = stringResource(R.string.new_group)
    val newChannel = stringResource(R.string.new_channel)
    val contacts = stringResource(R.string.contacts_title)
    val addAFriend = stringResource(R.string.add_a_friend)
    val alarmsOverview = stringResource(R.string.alarms_overview)
    val contactsManagement = stringResource(R.string.contacts_management)
    val settings = stringResource(R.string.settings_title)
// icons to mimic drawer destinations
    val items = remember {
        listOf(
            DrawerSection(
                title = "Alarms management",
                subsections = listOf(
                    DrawerSubSection(
                        title = alarmsOverview,
                        icon = Icons.Outlined.Home,
                        selected = true
                    ) { scope.launch { drawerState.close() } },
                    DrawerSubSection(
                        title = newGroup,
                        icon = Icons.Outlined.Groups
                    ) {
                        scope.launch { drawerState.close() }
                        onNavigate(ADD_GROUP)
                    },
                    DrawerSubSection(
                        title = newChannel,
                        icon = Icons.Outlined.Campaign
                    ) {
                        scope.launch { drawerState.close() }
                        //viewModel.onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_CHANNEL))
                        onNavigate(ADD_CHANNEL)

                    },
                )
            ),
            DrawerSection(
                title = contactsManagement,
                subsections = listOf(
                    DrawerSubSection(
                        title = contacts,
                        icon = Icons.Outlined.Contacts
                    ) {
                        scope.launch { drawerState.close() }
                        onNavigate(CONTACT_BROWSER)
                    },
                    DrawerSubSection(
                        title = addAFriend,
                        icon = Icons.Outlined.PersonAddAlt
                    ) {
                        scope.launch { drawerState.close() }
                        onNavigate(ADD_CONTACT)
                    },
                )
            ),
            DrawerSection(
                title = settings,
                subsections = listOf(
                    DrawerSubSection(
                        title = settings,
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
                    text = stringResource(R.string.app_name),
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
                    val density = LocalDensity.current

                    AnimatedVisibility(
                        visible = uiState.dialogContent != DialogContent.NONE,
                        enter = slideInHorizontally {
                            // Slide in from 40 dp from the top.
                            with(density) { 40.dp.roundToPx() }
                        } + fadeIn(
                            // Fade in with the initial alpha of 0.3f.
                            initialAlpha = 0.3f
                        ),
                        exit = slideOutHorizontally() + fadeOut()
                    ) {
                        BackHandler {
                            viewModel.onEvent(AlarmBrowserEvent.BackButtonPressed)
                        }
                        SinglePaneDialog(
                            onNavigate = onNavigate,
                            uiState = uiState,
                            onEvent = viewModel::onEvent
                        )
                    }


                    if (uiState.dialogContent == DialogContent.NONE) {
                        SinglePaneScreen(
                            modifier = modifier.systemBarsPadding(),
                            onEvent = viewModel::onEvent,
                            uiState = uiState,
                            onOpenDrawer = {
                                scope.launch { drawerState.open() }
                            },
                            onNavigate = onNavigate
                        )
                    }
                } else {
                    AlarmBrowserDualPaneContent(
                        onEvent = viewModel::onEvent,
                        uiState = uiState,
                        onOpenDrawer = {
                            scope.launch { drawerState.open() }
                        },
                        onNavigate = onNavigate
                    )
                    val density = LocalDensity.current

                    AnimatedVisibility(
                        visible = uiState.dialogContent != DialogContent.NONE,
                        enter = slideInVertically {
                            // Slide in from 40 dp from the top.
                            with(density) { -40.dp.roundToPx() }
                        } + expandVertically(
                            // Expand from the top.
                            expandFrom = Alignment.Top
                        ) + fadeIn(
                            // Fade in with the initial alpha of 0.3f.
                            initialAlpha = 0.3f
                        ),
                        exit = slideOutVertically() + shrinkVertically() + fadeOut()
                    ) {
                        DualPaneDialog(
                            onNavigate = onNavigate,
                            uiState = uiState,
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
        PreviewAlarmRepository(),
        chatService = ChatServiceImpl(),
        accountsService = FakeAccountsService(
            users = FakeAccountsService.defaultUsers.toMutableMap(),
            friendshipRequests = FakeAccountsService.defaultFriendshipRequests.toMutableMap()
        ),
        authService = FakeAuthService(),
        alarmProviderService = AlarmProviderServiceImpl()
    )
    AlarmBrowserScreen(viewModel = testViewModel)
}
