package dev.bebora.swecker.ui.alarm_browser.dual_pane

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlarmOff
import androidx.compose.material.icons.outlined.GroupOff
import androidx.compose.material.icons.outlined.PublicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.alarm_browser.DetailsScreenContent
import dev.bebora.swecker.ui.alarm_browser.NavBarDestination.*
import dev.bebora.swecker.ui.alarm_browser.alarm_details.AlarmDetailsScreen
import dev.bebora.swecker.ui.alarm_browser.channel_screen.ChannelAlarmListScreen
import dev.bebora.swecker.ui.alarm_browser.channel_screen.ChannelDetailsScreen
import dev.bebora.swecker.ui.alarm_browser.chat.ChatScreen
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupAlarmListScreen
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupDetailsScreen

@Composable
fun DualPaneContentDetails(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState
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

    if (uiState.animatedDetailsScreenContent == DetailsScreenContent.NONE) {
        AnimatedVisibility(
            visibleState = uiState.mutableTransitionState,
            enter = enterAnim,
            exit = exitAnim
        ) {
            Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center) {
                when (uiState.selectedDestination) {
                    HOME, PERSONAL -> {
                        if (uiState.loadingComplete && uiState.alarms.isEmpty()) {
                            Column() {
                                Icon(
                                    modifier = Modifier
                                        .size(128.dp),
                                    imageVector = Icons.Outlined.AlarmOff,
                                    contentDescription = "No alarms",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = stringResource(R.string.no_alarms_warning),
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                        } else {
                            Text(
                                text = stringResource(R.string.suggest_alarm_selection),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.displayMedium
                            )
                        }
                    }

                    GROUPS -> {
                        if (uiState.loadingComplete && uiState.groups.isEmpty()) {
                            Column() {
                                Icon(
                                    modifier = Modifier
                                        .size(128.dp),
                                    imageVector = Icons.Outlined.GroupOff,
                                    contentDescription = "No groups",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = stringResource(R.string.no_groups_warning),
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.suggest_group_selection),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.displayMedium
                            )
                        }
                    }
                    CHANNELS -> {
                        if (uiState.loadingComplete && uiState.channels.isEmpty()) {
                            Column() {
                                Icon(
                                    modifier = Modifier
                                        .size(128.dp),
                                    imageVector = Icons.Outlined.PublicOff,
                                    contentDescription = "No channels",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = stringResource(R.string.no_channels_warning),
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.suggest_channel_selection),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.displayMedium
                            )
                        }
                    }
                }
            }
        }
    } else {
        BackHandler {
            onEvent(AlarmBrowserEvent.BackButtonPressed)
        }
        AnimatedVisibility(
            visibleState = uiState.mutableTransitionState,
            enter = enterAnim,
            exit = exitAnim
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
                DetailsScreenContent.CHANNEL_DETAILS -> {
                    ChannelDetailsScreen(
                        modifier = modifier,
                        onEvent = onEvent,
                        uiState = uiState,
                        roundTopCorners = true
                    )
                }
                DetailsScreenContent.CHANNEL_ALARM_LIST -> {
                    ChannelAlarmListScreen(
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
