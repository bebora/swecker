package dev.bebora.swecker.ui.alarm_browser.dual_pane

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState

@Composable
fun AlarmBrowserDualPaneContent(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    onOpenDrawer: () -> Unit = {},
    uiState: AlarmBrowserUIState,
    onNavigate: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(1f)
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
    ) {
        AlarmBrowserNavRail(
            alarmBrowserUIState = uiState,
            onEvent = onEvent,
            onOpenDrawer = { onOpenDrawer() },
            onNavigate = onNavigate
        )
        DualPaneContentList(onEvent = onEvent, uiState = uiState)

        DualPaneContentDetails(
            modifier = modifier,
            onEvent = onEvent,
            uiState = uiState
        )

    }
}
