package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.alarm_browser.chat.ChatTopAppBar
import dev.bebora.swecker.ui.alarm_browser.group_screen.GroupTopAppBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
fun SweckerDetailsAppBar(
    modifier: Modifier = Modifier,
    uiState: AlarmBrowserUIState,
    colors: TopAppBarColors,
    roundTopCorners: Boolean = false,
    onEvent: (AlarmBrowserEvent) -> Unit,
) {
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
        when (uiState.detailsScreenContent) {
            DetailsScreenContent.ALARM_DETAILS -> {
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
            }
            DetailsScreenContent.GROUP_ALARM_LIST, DetailsScreenContent.GROUP_DETAILS -> {
                GroupTopAppBar(
                    colors = colors,
                    group = uiState.selectedGroup!!,
                    onEvent = onEvent,
                )
            }
            DetailsScreenContent.CHAT -> {
                ChatTopAppBar(
                    modifier = modifier,
                    colors = colors,
                    title = uiState.selectedAlarm?.name ?: "Hello world",
                    date = (uiState.selectedAlarm?.localDate ?: LocalDate.now()).format(
                        DateTimeFormatter.ofPattern("eee, dd MMM uuuu")
                    )
                        ?: "",
                    onEvent = onEvent,
                )
            }
            DetailsScreenContent.NONE -> {}
        }
    }
}


@Composable
fun SweckerTopAppBar(
    modifier: Modifier = Modifier,
    uiState: AlarmBrowserUIState,
    colors: TopAppBarColors,
    onOpenDrawer: () -> Unit,
    onEvent: (AlarmBrowserEvent) -> Unit,
) {
    SweckerDetailsAppBar(modifier = modifier, uiState = uiState, colors = colors, onEvent = onEvent)
    if (uiState.detailsScreenContent == DetailsScreenContent.NONE) {
        SweckerHomeTopAppBar(
            navigationAction = { onOpenDrawer() },
            colors = colors,
            searchAction = { /*TODO*/ },
            title = uiState.selectedDestination.toString().lowercase()
                .replaceFirstChar { it.uppercase() }
        )
    }
}
