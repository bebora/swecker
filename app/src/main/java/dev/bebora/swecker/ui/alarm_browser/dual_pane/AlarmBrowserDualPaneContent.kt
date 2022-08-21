package dev.bebora.swecker.ui.alarm_browser.dual_pane

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.alarm_browser.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmBrowserDualPaneContent(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    onOpenDrawer: () -> Unit = {},
    uiState: AlarmBrowserUIState
) {
    Row(
        modifier = modifier
            .fillMaxWidth(1f)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp)
    ) {
        AlarmBrowserNavRail(
            alarmBrowserUIState = uiState,
            onEvent = onEvent,
            onOpenDrawer = { onOpenDrawer() },
        )
        DualPaneContentList(onEvent = onEvent, uiState = uiState)
        Scaffold(
            topBar = {
                SweckerDetailsAppBar(
                    uiState = uiState,
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    onEvent = onEvent,
                    roundTopCorners = true
                )
            },
            floatingActionButton = {
                DetailsScreenFab(
                    detailsScreenContent = uiState.detailsScreenContent,
                    onEvent = onEvent
                )
            }

        ) {
            DualPaneContentDetails(
                modifier = modifier.padding(it),
                onEvent = onEvent,
                uiState = uiState
            )
        }
    }
}
