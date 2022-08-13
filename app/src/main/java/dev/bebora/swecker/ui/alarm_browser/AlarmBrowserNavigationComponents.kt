package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.AlarmRepositoryImpl
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.ui.theme.SweckerTheme

@Composable
fun SweckerNavBar(
    modifier: Modifier = Modifier,
    alarmBrowserUIState: AlarmBrowserUIState,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    val items = listOf("Home", "Personal", "Groups", "Channels")

    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            val isSelected =
                alarmBrowserUIState.selectedDestination == NavBarDestination.valueOf(item.uppercase())
            NavigationBarItem(
                icon = { Icon(getNavbarIcon(item, isSelected), contentDescription = item) },
                label = { Text(item) },
                selected = isSelected,
                onClick = { onEvent(AlarmBrowserEvent.NavBarNavigate(NavBarDestination.valueOf(item.uppercase()))) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NavBarPreview() {
    val testViewModel = AlarmBrowserViewModel(AlarmRepositoryImpl())
    val uiState by testViewModel.uiState.collectAsState()
    SweckerTheme() {
        Scaffold(bottomBar = {
            SweckerNavBar(
                alarmBrowserUIState = uiState,
                onEvent = { ev -> testViewModel.onEvent(ev) })
        }) {
            Box(modifier = Modifier.padding(it)) {
            }
        }
    }
}

@Composable
fun SweckerNavRail(
    modifier: Modifier = Modifier,
    alarmBrowserUIState: AlarmBrowserUIState,
    onEvent: (AlarmBrowserEvent) -> Unit,
) {

    val items = listOf("Home", "Personal", "Groups", "Channels")

    NavigationRail(
        modifier = modifier,
        header = {
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Open hamburger menu")
            }
            SweckerFab(destination = alarmBrowserUIState.selectedDestination) {
                onEvent(AlarmBrowserEvent.FabPressed)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        items.forEach { item ->
            val isSelected =
                alarmBrowserUIState.selectedDestination == NavBarDestination.valueOf(item.uppercase())
            NavigationRailItem(
                icon = { Icon(getNavbarIcon(item, isSelected), contentDescription = item) },
                label = { Text(item) },
                selected = isSelected,
                onClick = { onEvent(AlarmBrowserEvent.NavBarNavigate(NavBarDestination.valueOf(item.uppercase()))) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NavRailPreview() {
    val testViewModel = AlarmBrowserViewModel(AlarmRepositoryImpl())
    val uiState by testViewModel.uiState.collectAsState()
    SweckerTheme() {
        Scaffold(bottomBar = {
            SweckerNavRail(
                alarmBrowserUIState = uiState,
                onEvent = { ev -> testViewModel.onEvent(ev) }
            )
        }) {
            Box(modifier = Modifier.padding(it)) {
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SweckerHomeTopAppBar(
    modifier: Modifier = Modifier,
    colors: TopAppBarColors,
    navigationAction: () -> Unit,
    searchAction: () -> Unit,
    title: String,
) {
    SmallTopAppBar(
        colors = colors,
        modifier = modifier,
        title = { Text(text = title, textAlign = TextAlign.Center) },
        navigationIcon = {
            IconButton(onClick = { navigationAction() }) {
                Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Open hamburger menu")
            }
        },
        actions = {
            IconButton(onClick = { searchAction() }) {
                Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search content")
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SweckerGroupTopAppBar(
    modifier: Modifier = Modifier,
    group: Group,
    colors: TopAppBarColors,
    onEvent: (AlarmBrowserEvent) -> Unit,
    onClick: () -> Unit
) {
    SmallTopAppBar(
        colors = colors,
        modifier = modifier.clickable { onClick() },
        title =
        {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = group.name, textAlign = TextAlign.Left)
                Text(
                    modifier = modifier.padding(horizontal = 10.dp),
                    text = group.members?.size.toString().plus(" members"),
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
                Image(
                    painter = ColorPainter(MaterialTheme.colorScheme.tertiary),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        },
        actions = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SweckerTopAppBar(
    modifier: Modifier = Modifier,
    uiState: AlarmBrowserUIState,
    colors: TopAppBarColors,
    onEvent: (AlarmBrowserEvent) -> Unit,
) {
    if (uiState.isDetailsOpen) {
        SmallTopAppBar(
            colors = colors,
            modifier = modifier,
            title = { Text(text = "Alarm details", textAlign = TextAlign.Center) },
            navigationIcon = {
                IconButton(onClick = { onEvent(AlarmBrowserEvent.BackButtonPressed) }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            },
            actions = {
            })
    } else {
        if (uiState.isGroupOpen) {
            SweckerGroupTopAppBar(
                colors = colors,
                group = uiState.selectedGroup!!,
                onEvent = onEvent
            ) {}
        } else {
            SweckerHomeTopAppBar(
                navigationAction = { /*TODO*/ },
                colors = colors,
                searchAction = { /*TODO*/ },
                title = uiState.selectedDestination.toString().lowercase()
                    .replaceFirstChar { it.uppercase() }
            )
        }
    }
}
