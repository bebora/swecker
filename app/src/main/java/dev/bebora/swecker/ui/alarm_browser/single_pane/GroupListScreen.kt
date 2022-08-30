package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GroupOff
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
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
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupList

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun GroupListScreen(
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
        modifier = Modifier,
        topBar = {
            SmallTopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = modifier,
                title = { Text(text = stringResource(id = R.string.groups), textAlign = TextAlign.Center) },
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
                destination = NavBarDestination.GROUPS,
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
            if (uiState.loadingComplete && uiState.groups.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(1f),
                    contentAlignment = Alignment.Center
                ) {
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

                }
            } else {
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
                GroupList(
                    groups = uiState.groups.filter { group ->
                        group.name.contains(uiState.searchKey, ignoreCase = true)
                    },
                    onEvent = { group -> onEvent(AlarmBrowserEvent.GroupSelected(group)) },
                    selectedGroupId = uiState.selectedGroup?.id
                )
            }
        }
    }
}


