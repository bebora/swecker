package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserSearchBar
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.alarm_browser.NavBarDestination
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupListScreen(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState,
    navigationAction: () -> Unit,
) {
    var showSearchBar by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            SmallTopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = modifier,
                title = { Text(text = "Groups", textAlign = TextAlign.Center) },
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
                modifier = Modifier
                    .padding(32.dp),
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
            if (showSearchBar || uiState.searchKey.isNotEmpty()) {
                AlarmBrowserSearchBar(
                    modifier = Modifier.padding(4.dp),
                    searchKey = uiState.searchKey,
                    onValueChange = { newValue -> onEvent(AlarmBrowserEvent.SearchGroups(newValue)) })
            }
            GroupList(
                groups = uiState.groups.filter { group ->
                    group.name.contains(uiState.searchKey, ignoreCase = true)
                },
                onEvent = { group -> onEvent(AlarmBrowserEvent.ChannelSelected(group)) },
                selectedGroupId = uiState.selectedGroup?.id
            )
        }
    }
}


