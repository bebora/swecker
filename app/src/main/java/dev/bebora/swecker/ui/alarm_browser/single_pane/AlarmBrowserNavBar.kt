package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.bebora.swecker.data.alarm_browser.PreviewAlarmRepository
import dev.bebora.swecker.data.service.impl.ChatServiceImpl
import dev.bebora.swecker.data.service.testimpl.FakeAlarmProviderService
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.ui.alarm_browser.*
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.ui.utils.getNavbarIcon
import dev.bebora.swecker.R
import dev.bebora.swecker.util.TestConstants

@Composable
fun AlarmBrowserNavBar(
    modifier: Modifier = Modifier,
    alarmBrowserUIState: AlarmBrowserUIState,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    if (alarmBrowserUIState.detailsScreenContent == DetailsScreenContent.NONE) {
        NavigationBar(modifier = modifier) {
            var isSelected =
                alarmBrowserUIState.selectedDestination == NavBarDestination.HOME
            NavigationBarItem(
                modifier = Modifier.testTag(TestConstants.home),
                icon = {
                    Icon(
                        getNavbarIcon("Home", isSelected),
                        contentDescription = "All alarms overview"
                    )
                },
                label = { Text(stringResource(R.string.home)) },
                selected = isSelected,
                onClick = {
                    onEvent(
                        AlarmBrowserEvent.NavBarNavigate(
                            NavBarDestination.HOME
                        )
                    )
                }
            )
            isSelected =
                alarmBrowserUIState.selectedDestination == NavBarDestination.PERSONAL
            NavigationBarItem(
                modifier = Modifier.testTag(TestConstants.personal),
                icon = {
                    Icon(
                        getNavbarIcon("Personal", isSelected),
                        contentDescription = "Personal alarms"
                    )
                },
                label = { Text(stringResource(R.string.personal)) },
                selected = isSelected,
                onClick = {
                    onEvent(
                        AlarmBrowserEvent.NavBarNavigate(
                            NavBarDestination.PERSONAL
                        )
                    )
                }
            )
            isSelected =
                alarmBrowserUIState.selectedDestination == NavBarDestination.GROUPS
            NavigationBarItem(
                modifier = Modifier.testTag(TestConstants.groups),
                icon = { Icon(getNavbarIcon("Groups", isSelected), contentDescription = "Groups") },
                label = { Text(stringResource(id = R.string.groups)) },
                selected = isSelected,
                onClick = {
                    onEvent(
                        AlarmBrowserEvent.NavBarNavigate(
                            NavBarDestination.GROUPS
                        )
                    )
                }
            )
            isSelected =
                alarmBrowserUIState.selectedDestination == NavBarDestination.CHANNELS
            NavigationBarItem(
                modifier = Modifier.testTag(TestConstants.channels),
                icon = {
                    Icon(
                        getNavbarIcon("Channels", isSelected),
                        contentDescription = "Channels"
                    )
                },
                label = { Text(stringResource(id = R.string.channels)) },
                selected = isSelected,
                onClick = {
                    onEvent(
                        AlarmBrowserEvent.NavBarNavigate(
                            NavBarDestination.CHANNELS
                        )
                    )
                }
            )

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
