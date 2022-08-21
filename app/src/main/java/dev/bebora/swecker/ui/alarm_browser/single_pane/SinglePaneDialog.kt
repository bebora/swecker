package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.ui.add_alarm.AddAlarmScreen
import dev.bebora.swecker.ui.add_group.AddGroupScreen
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.DialogContent
import dev.bebora.swecker.ui.contact_browser.ContactBrowserScreen

@Composable
fun SinglePaneDialog(
    modifier: Modifier = Modifier,
    dialogContent: DialogContent,
    onNavigate: (String) -> Unit,
    group: Group? = null,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    when (dialogContent) {
        DialogContent.ADD_ALARM -> AddAlarmScreen(modifier = modifier.fillMaxSize(1f),
            group = group,
            onGoBack = { onEvent(AlarmBrowserEvent.BackButtonPressed) })
        DialogContent.CONTACT_BROWSER -> ContactBrowserScreen(
            onGoBack = { onEvent(AlarmBrowserEvent.BackButtonPressed) },
            onNavigate = onNavigate
        )
        DialogContent.ADD_GROUP -> AddGroupScreen(onGoBack = { onEvent(AlarmBrowserEvent.BackButtonPressed) })
        else -> {}
    }
}
