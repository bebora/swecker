package dev.bebora.swecker.ui.alarm_browser


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.alarmTypeToIcon
import dev.bebora.swecker.ui.theme.SweckerTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmCard(
    alarm: Alarm,
    modifier: Modifier = Modifier,
    onEvent: () -> Unit = {}
) {
    Card(
        onClick = onEvent,
        enabled = alarm.enabled,
        modifier = modifier
            .aspectRatio(ratio = 2.85f, matchHeightConstraintsFirst = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(1.0f)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(1.0f)
            ) {
                Text(
                    style = MaterialTheme.typography.displayLarge,
                    text = alarm.time
                )
                Switch(
                    checked = alarm.enabled,
                    onCheckedChange = { _ -> onEvent() },
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    text = alarm.name,
                    modifier = Modifier.width(90.dp)
                )
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Left,
                    text = alarm.date,
                    modifier = Modifier.width(120.dp)
                )
                Icon(
                    modifier = Modifier.requiredSize(30.dp),
                    imageVector = alarmTypeToIcon(alarm.alarmType, alarm.enabled),
                    contentDescription = alarm.alarmType.toString(),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AlarmPreview() {
    SweckerTheme {
        var alarm by remember {
            mutableStateOf(
                Alarm(
                    id = "@monesi#1",
                    name = "Alarm test",
                    time = "14:30",
                    date = "mon 7 December",
                    alarmType = AlarmType.PERSONAL
                )
            )
        }
        AlarmCard(
            alarm = alarm,
            onEvent = {
                alarm = alarm.copy(enabled = !alarm.enabled)
            }
        )
    }
}
