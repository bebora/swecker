package dev.bebora.swecker.ui.alarm_browser.channel_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.alarm_browser.*
import dev.bebora.swecker.ui.alarm_browser.single_pane.AlarmBrowserSinglePaneFab
import dev.bebora.swecker.ui.settings.account.PropicPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelAlarmListScreen(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState,
    roundTopCorners: Boolean
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Surface(
                shape = if (roundTopCorners) {
                    RoundedCornerShape(
                        topEnd = 20.dp,
                        topStart = 20.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                } else {
                    RectangleShape
                }
            ) {
                SmallTopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = if (uiState.me.id == uiState.selectedChannel?.owner) {
                        Modifier.clickable {
                            onEvent(AlarmBrowserEvent.DetailsOpened(type = DetailsScreenContent.CHANNEL_DETAILS))
                        }
                    } else {
                        Modifier
                    },
                    title =
                    {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = uiState.selectedChannel!!.name,
                                textAlign = TextAlign.Left,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = true
                            )
                            Text(
                                modifier = modifier.padding(horizontal = 10.dp),
                                text = uiState.selectedChannel.members.size.toString().plus(" ")
                                    .plus(stringResource(R.string.members)),
                                style = MaterialTheme.typography.labelSmall
                            )

                        }
                    },
                    navigationIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { onEvent(AlarmBrowserEvent.BackButtonPressed) }) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowBack,
                                    contentDescription = "Go back"
                                )
                            }

                            SubcomposeAsyncImage(
                                model = uiState.selectedChannel?.groupPicUrl,
                                loading = {
                                    PropicPlaceholder(
                                        size = 45.dp,
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    )
                                    {
                                        CircularProgressIndicator()
                                    }
                                },
                                error = {
                                    PropicPlaceholder(
                                        size = 45.dp,
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                    }
                                },
                                contentDescription = "Profile picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .requiredSize(45.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    },
                    actions = {})
            }
        },
        floatingActionButton = {
            if(uiState.selectedChannel?.owner == uiState.me.id) {
                AlarmBrowserSinglePaneFab(
                    destination = NavBarDestination.CHANNELS,
                    modifier = Modifier
                        .padding(32.dp),
                    detailsScreenContent = uiState.detailsScreenContent,
                    onEvent = onEvent
                )
            }
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            AlarmList(
                alarms = uiState.filteredAlarms!!,
                onEvent = onEvent,
                selectedAlarm = uiState.selectedAlarm
            )
        }
    }
}
