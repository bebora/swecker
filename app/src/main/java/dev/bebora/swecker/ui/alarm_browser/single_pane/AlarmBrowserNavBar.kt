package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bebora.swecker.data.alarm_browser.PreviewAlarmRepository
import dev.bebora.swecker.data.service.impl.ChatServiceImpl
import dev.bebora.swecker.data.service.testimpl.FakeAlarmProviderService
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.ui.alarm_browser.*
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.ui.utils.getNavbarIcon

@Composable
fun AlarmBrowserNavBar(
    modifier: Modifier = Modifier,
    alarmBrowserUIState: AlarmBrowserUIState,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    val items = listOf("Home", "Personal", "Groups", "Channels")

    if (alarmBrowserUIState.detailsScreenContent == DetailsScreenContent.NONE) {
        NavigationBar(modifier = modifier) {
            items.forEach { item ->
                val isSelected =
                    alarmBrowserUIState.selectedDestination == NavBarDestination.valueOf(item.uppercase())
                NavigationBarItem(
                    icon = { Icon(getNavbarIcon(item, isSelected), contentDescription = item) },
                    label = { Text(item) },
                    selected = isSelected,
                    onClick = {
                        onEvent(
                            AlarmBrowserEvent.NavBarNavigate(
                                NavBarDestination.valueOf(
                                    item.uppercase()
                                )
                            )
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NavBarPreview() {
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
            AlarmBrowserNavBar(
                alarmBrowserUIState = uiState,
                onEvent = { ev -> testViewModel.onEvent(ev) })
        }) {
            Box(modifier = Modifier.padding(it)) {
            }
        }
    }
}
