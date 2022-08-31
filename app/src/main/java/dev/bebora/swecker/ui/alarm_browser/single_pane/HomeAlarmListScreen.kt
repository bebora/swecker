package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlarmOff
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.alarm_browser.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeAlarmListScreen(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState,
    navigationAction: () -> Unit,
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
                title = { Text(text = stringResource(id = R.string.home), textAlign = TextAlign.Center) },
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
                            contentDescription = "Search content"
                        )
                    }
                })
        },
        floatingActionButton = {
            AlarmBrowserSinglePaneFab(
                destination = NavBarDestination.HOME,
                modifier = Modifier,
                detailsScreenContent = uiState.detailsScreenContent,
                onEvent = onEvent
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
                            AlarmBrowserEvent.SearchAlarms(
                                newValue
                            )
                        )
                    })
                BackHandler() {
                    showSearchBar = false
                }
            }
            if (uiState.loadingComplete && uiState.alarms.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(1f),
                    contentAlignment = Center
                ) {
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
                            modifier = Modifier.align(CenterHorizontally),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                }
            } else {
                AlarmList(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    alarms = uiState.filteredAlarms ?: uiState.alarms,
                    onEvent = onEvent,
                    selectedAlarm = uiState.selectedAlarm
                )
            }
        }
    }
}
