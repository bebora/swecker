package dev.bebora.swecker.ui.alarm_browser.dual_pane

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAlarm
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Composable
fun AlarmBrowserNavRail(
    modifier: Modifier = Modifier,
    alarmBrowserUIState: AlarmBrowserUIState,
    onOpenDrawer: () -> Unit,
    onEvent: (AlarmBrowserEvent) -> Unit,
    onNavigate: (String) -> Unit = {}
) {

    val items = listOf("Home", "Personal", "Groups", "Channels")

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
        items.forEach { item ->
            val isSelected =
                alarmBrowserUIState.selectedDestination == NavBarDestination.valueOf(item.uppercase())
            NavigationRailItem(
                icon = { Icon(getNavbarIcon(item, isSelected), contentDescription = item) },
                label = { Text(item) },
                selected = isSelected,
                onClick = { onEvent(AlarmBrowserEvent.NavBarNavigate(NavBarDestination.valueOf(item.uppercase()))) },
            )
        }
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
                imageVector = Icons.Outlined.AddAlert,
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
        accountsService = FakeAccountsService(),
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
