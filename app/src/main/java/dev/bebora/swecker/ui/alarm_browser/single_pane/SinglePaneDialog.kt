package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bebora.swecker.ui.add_alarm.AddAlarmScreen
import dev.bebora.swecker.ui.add_group.AddGroupScreen
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.alarm_browser.DialogContent
import dev.bebora.swecker.ui.contact_browser.ContactBrowserScreen

@Composable
fun SinglePaneDialog(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit,
    uiState: AlarmBrowserUIState,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    when (uiState.dialogContent) {
        DialogContent.ADD_ALARM -> AddAlarmScreen(modifier = modifier.fillMaxSize(1f),
            group = uiState.selectedGroup,
            userId = uiState.me.id,
            onGoBack = { onEvent(AlarmBrowserEvent.BackButtonPressed) })
        DialogContent.CONTACT_BROWSER -> ContactBrowserScreen(
            onGoBack = { onEvent(AlarmBrowserEvent.BackButtonPressed) },
            onNavigate = onNavigate
        )
        DialogContent.ADD_GROUP -> AddGroupScreen(onGoBack = { onEvent(AlarmBrowserEvent.BackButtonPressed) })
        else -> {}
    }
}
