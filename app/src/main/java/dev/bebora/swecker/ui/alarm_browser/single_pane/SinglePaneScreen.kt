package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.alarm_browser.DetailsScreenContent
import dev.bebora.swecker.ui.alarm_browser.NavBarDestination
import dev.bebora.swecker.ui.alarm_browser.alarm_details.AlarmDetailsScreen
import dev.bebora.swecker.ui.alarm_browser.channel_screen.ChannelAlarmListScreen
import dev.bebora.swecker.ui.alarm_browser.channel_screen.ChannelDetailsScreen
import dev.bebora.swecker.ui.alarm_browser.chat.ChatScreen
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupAlarmListScreen
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupDetailsScreen

@Composable
fun SinglePaneScreen(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState,
    onOpenDrawer: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val enterAnim = slideInHorizontally(
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        )
    ) { it } + fadeIn()

    val exitAnim = fadeOut(
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        )
    )

    when (uiState.animatedDetailsScreenContent) {
        DetailsScreenContent.NONE -> {
            AnimatedVisibility(
                visibleState = uiState.mutableTransitionState,
                enter = enterAnim,
                exit = exitAnim
            ) {
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
                        navigationAction = onOpenDrawer,
                        onNavigate = onNavigate
                    )
                    NavBarDestination.CHANNELS -> ChannelListScreen(
                        modifier = modifier,
                        onEvent = onEvent,
                        uiState = uiState,
                        navigationAction = onOpenDrawer,
                        onNavigate = onNavigate
                    )
                }
            }
        }
        DetailsScreenContent.GROUP_DETAILS -> {
            BackHandler {
                onEvent(AlarmBrowserEvent.BackButtonPressed)
            }
            AnimatedVisibility(
                visibleState = uiState.mutableTransitionState,
                enter = enterAnim,
                exit = exitAnim
            ) {
                GroupDetailsScreen(
                    modifier = modifier,
                    onEvent = onEvent,
                    uiState = uiState,
                    roundTopCorners = false
                )
            }
        }
        DetailsScreenContent.CHANNEL_DETAILS -> {
            BackHandler {
                onEvent(AlarmBrowserEvent.BackButtonPressed)
            }
            AnimatedVisibility(
                visibleState = uiState.mutableTransitionState,
                enter = enterAnim,
                exit = exitAnim
            ) {
                ChannelDetailsScreen(
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
                enter = enterAnim,
                exit = exitAnim
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
                enter = enterAnim,
                exit = exitAnim
            ) {
                GroupAlarmListScreen(
                    modifier = modifier,
                    onEvent = onEvent,
                    uiState = uiState,
                    roundTopCorners = false

                )
            }
        }
        DetailsScreenContent.CHANNEL_ALARM_LIST -> {
            BackHandler {
                onEvent(AlarmBrowserEvent.BackButtonPressed)
            }
            AnimatedVisibility(
                visibleState = uiState.mutableTransitionState,
                enter = enterAnim,
                exit = exitAnim
            ) {
                ChannelAlarmListScreen(
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
                enter = enterAnim,
                exit = exitAnim
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

    if (uiState.mutableTransitionState.isIdle) {
        onEvent(AlarmBrowserEvent.OnTransitionCompleted)
    }
}

