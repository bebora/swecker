package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.alarm_browser.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PersonalAlarmListScreen(
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
                title = { Text(text = "Personal", textAlign = TextAlign.Center) },
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
                destination = NavBarDestination.PERSONAL,
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
                    onValueChange = { newValue -> onEvent(AlarmBrowserEvent.SearchAlarms(newValue)) })
                BackHandler() {
                    showSearchBar = false
                }
            }

            AlarmList(
                alarms = uiState.filteredAlarms ?: uiState.alarms,
                onEvent = onEvent,
                selectedAlarm = uiState.selectedAlarm
            )
        }
    }
}
