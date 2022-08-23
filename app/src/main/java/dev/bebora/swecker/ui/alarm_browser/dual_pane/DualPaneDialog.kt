package dev.bebora.swecker.ui.alarm_browser.dual_pane

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bebora.swecker.ui.add_alarm.AddAlarmDialog
import dev.bebora.swecker.ui.add_group.AddGroupDialog
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.alarm_browser.DialogContent
import dev.bebora.swecker.ui.contact_browser.ContactBrowserDialog

@Composable
fun DualPaneDialog(
    modifier: Modifier = Modifier,
    uiState: AlarmBrowserUIState,
    onNavigate: (String) -> Unit,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    when (uiState.dialogContent) {
        DialogContent.ADD_ALARM -> AddAlarmDialog(modifier = modifier,
            group = uiState.selectedGroup,
            userId = uiState.me.id,
            onGoBack = { onEvent(AlarmBrowserEvent.BackButtonPressed) })
        DialogContent.CONTACT_BROWSER -> ContactBrowserDialog(
            onGoBack = { onEvent(AlarmBrowserEvent.BackButtonPressed) },
            onNavigate = onNavigate
        )
        DialogContent.ADD_GROUP -> AddGroupDialog(
            onGoBack = { onEvent(AlarmBrowserEvent.BackButtonPressed) },
        )
        else -> {}
    }
}
