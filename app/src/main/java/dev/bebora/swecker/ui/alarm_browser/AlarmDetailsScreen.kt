package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.ui.theme.SweckerTheme


//TODO add missing behaviour and options
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDetails(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    isReadOnly: Boolean,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(1f),
        ) {
            OutlinedTextField(
                label = { Text("Date") },
                placeholder = { Text("Select date") },
                value = alarm.date,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.EditCalendar,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .weight(0.5f)
                    .clickable { } //add calendar action for picking date
                    .padding(4.dp),
            )

            OutlinedTextField(
                label = { Text("Time") },
                placeholder = { Text("Select time") },
                value = alarm.time,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .weight(0.5f)
                    .clickable { } //add action for picking time
                    .padding(4.dp),
            )
        }
        OutlinedTextField(
            label = { Text("Name") },
            leadingIcon = { Icon(imageVector = Icons.Outlined.Label, contentDescription = null) },
            value = alarm.name,
            onValueChange = { newName ->
                onEvent(
                    AlarmBrowserEvent.AlarmPartiallyUpdated(
                        alarm.copy(
                            name = newName
                        )
                    )
                )
            },
            readOnly = isReadOnly,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(4.dp)
        )
        if (alarm.group != null) {

        }
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Outlined.Message, contentDescription = null)
            Text(
                text = "Enable chat",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(checked = alarm.enableChat,
                onCheckedChange = {
                    onEvent(
                        AlarmBrowserEvent.AlarmPartiallyUpdated(
                            alarm = alarm.copy(enableChat = it)
                        )
                    )
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                modifier = Modifier
                    .padding(4.dp),
                onClick = ({
                    onEvent(
                        AlarmBrowserEvent.AlarmUpdated(
                            alarm = alarm,
                            success = false
                        )
                    )
                })
            ) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(4.dp))
            OutlinedButton(
                modifier = Modifier
                    .padding(4.dp),
                onClick = ({
                    onEvent(
                        AlarmBrowserEvent.AlarmUpdated(
                            alarm = alarm,
                            success = true
                        )
                    )
                })
            ) {
                Text("Ok")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmDetailsPreview() {
    SweckerTheme() {
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
        AlarmDetails(alarm = alarm, isReadOnly = false, onEvent = {})
    }
}
