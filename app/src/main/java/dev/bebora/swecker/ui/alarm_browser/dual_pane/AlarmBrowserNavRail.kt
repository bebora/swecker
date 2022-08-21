package dev.bebora.swecker.ui.alarm_browser.dual_pane

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import dev.bebora.swecker.data.alarm_browser.AlarmRepositoryTestImpl
import dev.bebora.swecker.data.service.impl.AccountsServiceImpl
import dev.bebora.swecker.data.service.impl.AuthServiceImpl
import dev.bebora.swecker.data.service.impl.ChatServiceImpl
import dev.bebora.swecker.ui.alarm_browser.*
import dev.bebora.swecker.ui.theme.SweckerTheme

@Composable
fun AlarmBrowserNavRail(
    modifier: Modifier = Modifier,
    alarmBrowserUIState: AlarmBrowserUIState,
    onOpenDrawer: () -> Unit,
    onEvent: (AlarmBrowserEvent) -> Unit,
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
                onEvent = onEvent
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
fun AlarmBrowserNavRailFab(
    modifier: Modifier = Modifier,
    destination: NavBarDestination,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    FloatingActionButton(modifier = modifier, onClick = {
        when (destination) {
            NavBarDestination.HOME, NavBarDestination.PERSONAL -> {
                onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_ALARM))
            }
            NavBarDestination.GROUPS -> {
                onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_GROUP))
            }
            NavBarDestination.CHANNELS -> {
                onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_CHANNEL))
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
        AlarmRepositoryTestImpl(),
        chatService = ChatServiceImpl(),
        accountsService = AccountsServiceImpl(),
        authService = AuthServiceImpl()
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
