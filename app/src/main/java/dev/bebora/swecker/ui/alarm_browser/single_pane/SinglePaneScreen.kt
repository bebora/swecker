package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.alarm_browser.DetailsScreenContent
import dev.bebora.swecker.ui.alarm_browser.NavBarDestination
import dev.bebora.swecker.ui.alarm_browser.alarm_details.AlarmDetailsScreen
import dev.bebora.swecker.ui.alarm_browser.chat.ChatScreen
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupAlarmListScreen
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupDetailsScreen

@Composable
fun SinglePaneScreen(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState,
    onOpenDrawer: () -> Unit,
) {
    when (uiState.detailsScreenContent) {
        DetailsScreenContent.NONE -> {
            when (uiState.selectedDestination) {
                NavBarDestination.HOME -> HomeAlarmListScreen(
                    modifier = modifier,
                    onEvent = onEvent,
                    uiState = uiState,
                    navigationAction = onOpenDrawer
                )
                NavBarDestination.PERSONAL -> PersonalAlarmListScreen(
                    modifier = modifier,
                    onEvent = onEvent,
                    uiState = uiState,
                    navigationAction = onOpenDrawer
                )
                NavBarDestination.GROUPS -> GroupListScreen(
                    modifier = modifier,
                    onEvent = onEvent,
                    uiState = uiState,
                    navigationAction = onOpenDrawer
                )
                else -> {}
            }
        }
        DetailsScreenContent.GROUP_DETAILS -> {
            GroupDetailsScreen(
                modifier = modifier,
                onEvent = onEvent,
                uiState = uiState,
                roundTopCorners = false
            )
        }
        DetailsScreenContent.ALARM_DETAILS -> {
            AlarmDetailsScreen(
                modifier = modifier,
                onEvent = onEvent,
                uiState = uiState,
                roundTopCorners = false
            )
        }
        DetailsScreenContent.GROUP_ALARM_LIST -> {
            GroupAlarmListScreen(
                modifier = modifier,
                onEvent = onEvent,
                uiState = uiState,
                roundTopCorners = false

            )
        }
        DetailsScreenContent.CHAT -> {
            ChatScreen(
                modifier = modifier,
                onEvent = onEvent,
                uiState = uiState,
                roundTopCorners = false
            )
        }
    }
}
