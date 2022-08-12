package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.local.LocalAlarmDataProvider
import dev.bebora.swecker.ui.theme.SweckerTheme


@Composable
fun AlarmList(
    alarms: List<Alarm>,
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = alarms, key = { al -> al.id }) { al ->
            AlarmCard(alarm = al, modifier = modifier, onEvent = onEvent)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmListPreview() {
    SweckerTheme() {
        AlarmList(alarms = LocalAlarmDataProvider.allAlarms, onEvent = { })
    }
}

@Composable
fun GroupList(
    modifier: Modifier = Modifier,
    groups: List<Group>,
    onEvent: (AlarmBrowserEvent) -> Unit,
) {
    LazyColumn() {
        items(groups) { group ->
            GroupItem(
                modifier = modifier,
                group = group,
                firstAlarm = group.alarms.first(),
                onEvent = onEvent
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupListPreview() {
    SweckerTheme() {
        GroupList(groups = listOf(
            Group(
                1,
                "Wanda the group",
                alarms = LocalAlarmDataProvider.allAlarms,
                members = null,
                owner = "@me"
            ),
            Group(
                2,
                "Another group",
                alarms = LocalAlarmDataProvider.allAlarms,
                members = null,
                owner = "@you"
            ),
            Group(
                3,
                "A third group! Very long title",
                alarms = LocalAlarmDataProvider.allAlarms,
                members = null,
                owner = "@you"
            ),
        ), onEvent = {})
    }
}
