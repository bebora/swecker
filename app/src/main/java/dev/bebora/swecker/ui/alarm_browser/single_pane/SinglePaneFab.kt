package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAlarm
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.DetailsScreenContent
import dev.bebora.swecker.ui.alarm_browser.DialogContent
import dev.bebora.swecker.ui.alarm_browser.NavBarDestination

@Composable
fun AlarmBrowserSinglePaneFab(
    modifier: Modifier = Modifier,
    destination: NavBarDestination,
    detailsScreenContent: DetailsScreenContent,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    when (detailsScreenContent) {
        DetailsScreenContent.NONE -> {
            FloatingActionButton(modifier = modifier, onClick = {
                when (destination) {
                    NavBarDestination.HOME, NavBarDestination.PERSONAL -> {
                        onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_ALARM))
                    }
                    NavBarDestination.GROUPS -> {
                        onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_GROUP))
                    }
                    NavBarDestination.CHANNELS -> {
                        onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_CHANNEL))
                    }
                }
            }) {
                when (destination) {
                    NavBarDestination.HOME, NavBarDestination.PERSONAL -> Icon(
                        imageVector = Icons.Outlined.AddAlarm,
                        contentDescription = null
                    )
                    NavBarDestination.GROUPS -> Icon(
                        imageVector = Icons.Outlined.GroupAdd,
                        contentDescription = null
                    )
                    NavBarDestination.CHANNELS -> Icon(
                        imageVector = Icons.Outlined.AddAlert,
                        contentDescription = null
                    )
                }
            }
        }
        DetailsScreenContent.GROUP_ALARM_LIST -> {
            FloatingActionButton(onClick = {
                onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_ALARM))
            }) {
                Icon(
                    imageVector = Icons.Outlined.AddAlarm,
                    contentDescription = null
                )
            }
        }
        else -> {
        }
    }
}
