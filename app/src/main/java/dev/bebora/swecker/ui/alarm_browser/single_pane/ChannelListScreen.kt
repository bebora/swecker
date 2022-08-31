package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserSearchBar
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.alarm_browser.NavBarDestination
import dev.bebora.swecker.ui.alarm_browser.channel_screen.ChannelList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ChannelListScreen(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState,
    navigationAction: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var showSearchBar by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember { FocusRequester() }

    if (showSearchBar) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SmallTopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier,
                title = {
                    Text(
                        text = stringResource(R.string.channels),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigationAction() }) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Open hamburger menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showSearchBar = !showSearchBar
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search channels"
                        )
                    }
                })
        },
        floatingActionButton = {
            AlarmBrowserSinglePaneFab(
                destination = NavBarDestination.CHANNELS,
                modifier = Modifier,
                detailsScreenContent = uiState.detailsScreenContent,
                onEvent = onEvent,
                onNavigate = onNavigate
            )
        },
        bottomBar = {
            AlarmBrowserNavBar(
                alarmBrowserUIState = uiState,
                onEvent = onEvent,
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            AnimatedVisibility(
                visible = showSearchBar || uiState.searchKey.isNotEmpty(),
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                AlarmBrowserSearchBar(
                    modifier = Modifier
                        .padding(4.dp)
                        .focusRequester(focusRequester),
                    searchKey = uiState.searchKey,
                    onValueChange = { newValue ->
                        onEvent(
                            AlarmBrowserEvent.SearchGroups(
                                newValue
                            )
                        )
                    })
                BackHandler() {
                    showSearchBar = false
                }
            }
            if (uiState.loadingComplete && uiState.channels.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(1f),
                    contentAlignment = Alignment.Center
                ) {
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

                }
            } else {
                ChannelList(
                    channels = uiState.channels.filter { channel ->
                        channel.name.contains(uiState.searchKey, ignoreCase = true)
                    } + uiState.extraChannels,
                    myId = uiState.me.id,
                    onEvent = { channel -> onEvent(AlarmBrowserEvent.ChannelSelected(channel)) },
                    onChannelJoin = { channel -> onEvent(AlarmBrowserEvent.JoinChannel(channel)) },
                    selectedChannelId = uiState.selectedChannel?.id
                )
            }
        }
    }
}
