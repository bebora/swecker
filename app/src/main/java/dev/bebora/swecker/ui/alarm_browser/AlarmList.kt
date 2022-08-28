package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.local.LocalAlarmDataProvider
import dev.bebora.swecker.ui.theme.SweckerTheme

@Composable
fun AlarmList(
    alarms: List<Alarm>,
    modifier: Modifier = Modifier,
    selectedAlarm: Alarm? = null,
    onEvent: (AlarmBrowserEvent) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(1f)
    ) {
        items(items = alarms, key = { al -> al.id }) { al ->
            var selected = false
            if (selectedAlarm != null) {
                selected =
                    al.id == selectedAlarm.id
            }
            AlarmCard(
                alarm = al, modifier = if (selected) {
                    modifier
                } else {
                    modifier.padding(horizontal = 8.dp)
                }, onEvent = onEvent, selected = selected
            )

            Spacer(modifier = Modifier.height(12.dp))

        }
        item() {
            Spacer(modifier = Modifier.height(64.dp))
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

