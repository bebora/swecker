package dev.bebora.swecker.ui.alarm_browser.single_pane

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddAlarm
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.DetailsScreenContent
import dev.bebora.swecker.ui.alarm_browser.DialogContent
import dev.bebora.swecker.ui.alarm_browser.NavBarDestination
import dev.bebora.swecker.util.ADD_CHANNEL
import dev.bebora.swecker.util.ADD_GROUP
import dev.bebora.swecker.util.TestConstants

@Composable
fun AlarmBrowserSinglePaneFab(
    modifier: Modifier = Modifier,
    destination: NavBarDestination,
    detailsScreenContent: DetailsScreenContent,
    onEvent: (AlarmBrowserEvent) -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    when (detailsScreenContent) {
        DetailsScreenContent.NONE -> {
            FloatingActionButton(modifier = modifier.testTag(TestConstants.fab), onClick = {
                when (destination) {
                    NavBarDestination.HOME, NavBarDestination.PERSONAL -> {
                        onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_ALARM))
                    }
                    NavBarDestination.GROUPS -> {
                        //onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_GROUP))
                        onNavigate(ADD_GROUP)
                    }
                    NavBarDestination.CHANNELS -> {
                        //onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_CHANNEL))
                        onNavigate(ADD_CHANNEL)
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
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null
                    )
                }
            }
        }
        DetailsScreenContent.GROUP_ALARM_LIST -> {
            FloatingActionButton(
                modifier = modifier,
                onClick = {
                    onEvent(AlarmBrowserEvent.DialogOpened(DialogContent.ADD_ALARM))
                }) {
                Icon(
                    imageVector = Icons.Outlined.AddAlarm,
                    contentDescription = null
                )
            }
        }
        DetailsScreenContent.CHANNEL_ALARM_LIST -> {
            FloatingActionButton(
                modifier = modifier,
                onClick = {
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
