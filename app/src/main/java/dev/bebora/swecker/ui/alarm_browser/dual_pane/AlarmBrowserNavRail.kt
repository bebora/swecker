package dev.bebora.swecker.ui.alarm_browser.dual_pane

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.R
import dev.bebora.swecker.data.alarm_browser.PreviewAlarmRepository
import dev.bebora.swecker.data.service.impl.ChatServiceImpl
import dev.bebora.swecker.data.service.testimpl.FakeAlarmProviderService
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.ui.alarm_browser.*
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.ui.utils.getNavbarIcon
import dev.bebora.swecker.util.ADD_CHANNEL
import dev.bebora.swecker.util.ADD_GROUP
import dev.bebora.swecker.util.TestConstants

@Composable
fun AlarmBrowserNavRail(
    modifier: Modifier = Modifier,
    alarmBrowserUIState: AlarmBrowserUIState,
    onOpenDrawer: () -> Unit,
    onEvent: (AlarmBrowserEvent) -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    NavigationRail(
        modifier = modifier,
        header = {
            IconButton(
                onClick = {
                    Log.d("SWECKER_NAV", "Devo aprire drawer")
                    onOpenDrawer()
                }) {
                Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Open hamburger menu")
            }
            AlarmBrowserNavRailFab(
                modifier = Modifier.testTag(TestConstants.fab),
                destination = alarmBrowserUIState.selectedDestination,
                onEvent = onEvent,
                onNavigate = onNavigate
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(
            modifier = Modifier.requiredHeightIn(300.dp, 400.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            var isSelected =
                alarmBrowserUIState.selectedDestination == NavBarDestination.HOME
            NavigationRailItem(
                modifier = Modifier.testTag(TestConstants.home),
                icon = { Icon(getNavbarIcon("Home", isSelected), contentDescription = "Home") },
                label = { Text(stringResource(R.string.home)) },
                selected = isSelected,
                onClick = { onEvent(AlarmBrowserEvent.NavBarNavigate(NavBarDestination.HOME)) },
            )

            isSelected =
                alarmBrowserUIState.selectedDestination == NavBarDestination.PERSONAL
            NavigationRailItem(
                modifier = Modifier.testTag(TestConstants.personal),
                icon = {
                    Icon(
                        getNavbarIcon("Personal", isSelected),
                        contentDescription = "Personal"
                    )
                },
                label = { Text(stringResource(R.string.personal)) },
                selected = isSelected,
                onClick = { onEvent(AlarmBrowserEvent.NavBarNavigate(NavBarDestination.PERSONAL)) },
            )

            isSelected =
                alarmBrowserUIState.selectedDestination == NavBarDestination.GROUPS
            NavigationRailItem(
                modifier = Modifier.testTag(TestConstants.groups),
                icon = { Icon(getNavbarIcon("Groups", isSelected), contentDescription = "Groups") },
                label = { Text(stringResource(R.string.groups)) },
                selected = isSelected,
                onClick = { onEvent(AlarmBrowserEvent.NavBarNavigate(NavBarDestination.GROUPS)) },
            )

            isSelected =
                alarmBrowserUIState.selectedDestination == NavBarDestination.CHANNELS
            NavigationRailItem(
                modifier = Modifier.testTag(TestConstants.channels),
                icon = {
                    Icon(
                        getNavbarIcon("Channels", isSelected),
                        contentDescription = "Channels"
                    )
                },
                label = { Text(stringResource(R.string.channels)) },
                selected = isSelected,
                onClick = { onEvent(AlarmBrowserEvent.NavBarNavigate(NavBarDestination.CHANNELS)) },
            )

        }
    }
}

@Composable
fun AlarmBrowserNavRailFab(
    modifier: Modifier = Modifier,
    destination: NavBarDestination,
    onEvent: (AlarmBrowserEvent) -> Unit,
    onNavigate: (String) -> Unit
) {
    FloatingActionButton(modifier = modifier, onClick = {
        when (destination) {
            NavBarDestination.HOME, NavBarDestination.PERSONAL -> {
                onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_ALARM))
            }
            NavBarDestination.GROUPS -> {
                //onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_GROUP))
                onNavigate(ADD_GROUP)
            }
            NavBarDestination.CHANNELS -> {
                //onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_CHANNEL))
                onNavigate(ADD_CHANNEL)
            }
        }
    }) {
        when (destination) {
            NavBarDestination.HOME, NavBarDestination.PERSONAL -> Icon(
                imageVector = Icons.Outlined.AddAlarm,
                contentDescription = null
            )
            NavBarDestination.GROUPS -> Icon(
                imageVector = Icons.Outlined.GroupAdd,
                contentDescription = null
            )
            NavBarDestination.CHANNELS -> Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NavRailPreview() {
    val testViewModel = AlarmBrowserViewModel(
        PreviewAlarmRepository(),
        chatService = ChatServiceImpl(),
        accountsService = FakeAccountsService(
            users = FakeAccountsService.defaultUsers.toMutableMap(),
            friendshipRequests = FakeAccountsService.defaultFriendshipRequests.toMutableMap()
        ),
        authService = FakeAuthService(),
        alarmProviderService = FakeAlarmProviderService()
    )
    val uiState = testViewModel.uiState
    SweckerTheme() {
        Scaffold(bottomBar = {
            AlarmBrowserNavRail(
                alarmBrowserUIState = uiState,
                onEvent = { ev -> testViewModel.onEvent(ev) },
                onOpenDrawer = {}
            )
        }) {
            Box(modifier = Modifier.padding(it)) {
            }
        }
    }
}
