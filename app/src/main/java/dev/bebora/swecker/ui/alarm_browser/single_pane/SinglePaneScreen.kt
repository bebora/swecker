package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
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
    when (uiState.animatedDetailsScreenContent) {
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
            BackHandler {
                onEvent(AlarmBrowserEvent.BackButtonPressed)
            }
            AnimatedVisibility(
                visibleState = uiState.mutableTransitionState,
                enter = slideInHorizontally { it } + fadeIn(),
                exit = slideOutHorizontally { -it } + fadeOut()
            ) {
                GroupDetailsScreen(
                    modifier = modifier,
                    onEvent = onEvent,
                    uiState = uiState,
                    roundTopCorners = false
                )
            }
        }
        DetailsScreenContent.ALARM_DETAILS -> {
            BackHandler {
                onEvent(AlarmBrowserEvent.BackButtonPressed)
            }
            AnimatedVisibility(
                visibleState = uiState.mutableTransitionState,
                enter = slideInHorizontally { it/2 } + fadeIn(),
                exit = slideOutHorizontally { it/2 } + fadeOut()
            ) {
                AlarmDetailsScreen(
                    modifier = modifier,
                    onEvent = onEvent,
                    uiState = uiState,
                    roundTopCorners = false
                )
            }
        }
        DetailsScreenContent.GROUP_ALARM_LIST -> {
            BackHandler {
                onEvent(AlarmBrowserEvent.BackButtonPressed)
            }
            AnimatedVisibility(
                visibleState = uiState.mutableTransitionState,
                enter = slideInHorizontally { -it/2 } + fadeIn(),
                exit = slideOutHorizontally { it/2 } + fadeOut()
            ) {
                GroupAlarmListScreen(
                    modifier = modifier,
                    onEvent = onEvent,
                    uiState = uiState,
                    roundTopCorners = false

                )
            }
        }
        DetailsScreenContent.CHAT -> {
            BackHandler {
                onEvent(AlarmBrowserEvent.BackButtonPressed)
            }
            AnimatedVisibility(
                visibleState = uiState.mutableTransitionState,
                enter = slideInHorizontally { it/2 } + fadeIn(),
                exit = slideOutHorizontally { -it/2 } + fadeOut()
            ) {
                ChatScreen(
                    modifier = modifier,
                    onEvent = onEvent,
                    uiState = uiState,
                    roundTopCorners = false
                )
            }
        }
    }

    if(uiState.mutableTransitionState.isIdle){
        onEvent(AlarmBrowserEvent.OnTransitionCompleted)
    }
}

