package dev.bebora.swecker.ui.alarm_browser.dual_pane

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.alarm_browser.DetailsScreenContent
import dev.bebora.swecker.ui.alarm_browser.alarm_details.AlarmDetailsScreen
import dev.bebora.swecker.ui.alarm_browser.chat.ChatScreen
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupAlarmListScreen
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupDetailsScreen

@Composable
fun DualPaneContentDetails(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState
) {

    if (uiState.animatedDetailsScreenContent == DetailsScreenContent.NONE) {
        AnimatedVisibility(
            visibleState = uiState.mutableTransitionState,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "Select something",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }
    } else {
        AnimatedVisibility(
            visibleState = uiState.mutableTransitionState,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            when (uiState.animatedDetailsScreenContent) {
                DetailsScreenContent.ALARM_DETAILS -> {
                    AlarmDetailsScreen(
                        modifier = modifier,
                        onEvent = onEvent,
                        uiState = uiState,
                        roundTopCorners = true
                    )
                }
                DetailsScreenContent.GROUP_ALARM_LIST -> {
                    GroupAlarmListScreen(
                        modifier = modifier,
                        onEvent = onEvent,
                        uiState = uiState,
                        roundTopCorners = true
                    )
                }
                DetailsScreenContent.GROUP_DETAILS -> {
                    GroupDetailsScreen(
                        modifier = modifier,
                        onEvent = onEvent,
                        uiState = uiState,
                        roundTopCorners = true
                    )
                }
                DetailsScreenContent.CHAT -> {
                    ChatScreen(
                        modifier = modifier,
                        onEvent = onEvent,
                        uiState = uiState,
                        roundTopCorners = true
                    )
                }
                else -> {
                }
            }
        }
    }
    if (uiState.mutableTransitionState.isIdle) {
        onEvent(AlarmBrowserEvent.OnTransitionCompleted)
    }
}
