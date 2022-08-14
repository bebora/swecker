package dev.bebora.swecker.ui.alarm_browser.alarm_details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.theme.SweckerTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDetails(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    isReadOnly: Boolean,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }


    Box {
        Column(
            modifier = modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ClickableOutlinedDetails(
                    modifier = Modifier
                        .weight(0.5f),
                    label = "Time",
                    placeHolder = "Select time",
                    value = alarm.time,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null
                        )
                    },
                    onClick = { showTimePicker = true }
                )

                ClickableOutlinedDetails(
                    modifier = Modifier
                        .weight(0.5f),
                    label = "Date",
                    placeHolder = "Select date",
                    value = alarm.date,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.EditCalendar,
                            contentDescription = null
                        )
                    },
                    onClick = { showDatePicker = true }
                )
            }
            OutlinedTextField(
                label = { Text("Name") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Label,
                        contentDescription = null
                    )
                },
                value = alarm.name,
                onValueChange = { newName ->
                    onEvent(AlarmBrowserEvent.AlarmPartiallyUpdated(alarm.copy(name = newName)))
                },
                readOnly = isReadOnly,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(1f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Divider()

            Text(
                text = "Repeat on",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            RepetitionDaysSelection(enabledDays = alarm.enabledDays,
                onClick = { index, enabled ->
                    onEvent(
                        AlarmBrowserEvent.AlarmPartiallyUpdated(
                            alarm.copy(
                                enabledDays = alarm.enabledDays.toMutableList()
                                    .also { it[index] = enabled })
                        )
                    )
                })
            Spacer(modifier = Modifier.height(4.dp))
            Divider()

            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .clickable {
                        onEvent(AlarmBrowserEvent.AlarmPartiallyUpdated(alarm.copy(enableChat = !alarm.enableChat)))
                    }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Outlined.Message, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Enable chat",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.weight(1f))
                OutlinedIconToggleButton(
                    modifier = Modifier.size(30.dp),
                    checked = alarm.enableChat,
                    border = if (alarm.enableChat) {
                        null
                    } else {
                        BorderStroke(4.dp, MaterialTheme.colorScheme.outlineVariant)
                    },
                    colors = if (alarm.enableChat) {
                        IconButtonDefaults.iconToggleButtonColors(
                            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        IconButtonDefaults.iconToggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    },
                    onCheckedChange = {
                        onEvent(
                            AlarmBrowserEvent.AlarmPartiallyUpdated(
                                alarm.copy(
                                    enableChat = !alarm.enableChat
                                )
                            )
                        )
                    },
                ) {
                    if (alarm.enableChat) {
                        Icon(imageVector = Icons.Outlined.Check, contentDescription = null)
                    }
                }
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
                                alarm = alarm.copy(),
                                success = true
                            )
                        )
                    })
                ) {
                    Text("Ok")
                }
            }

        }
        if (showDatePicker)
            DatePicker(onDateSelected = { newDate ->
                onEvent(AlarmBrowserEvent.AlarmPartiallyUpdated(alarm.copy(date = newDate)))
            }, onDismissRequest = {
                showDatePicker = false
            })
        if (showTimePicker)
            TimePicker(onTimeSelected = { newTime ->
                onEvent(AlarmBrowserEvent.AlarmPartiallyUpdated(alarm.copy(time = newTime)))
            }, onDismissRequest = {
                showTimePicker = false
            })
    }
}


@Composable
fun RepetitionDaysSelection(
    modifier: Modifier = Modifier,
    onClick: (Int, Boolean) -> Unit,
    enabledDays: List<Boolean>
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(1f)
    ) {
        val days = getDaysOfWeekDisplayNames(loc = Locale.getDefault())
        days.forEachIndexed { index, day ->

            OutlinedButton(
                modifier = Modifier.size(40.dp),
                onClick = {
                    onClick(index, !enabledDays[index])
                },
                contentPadding = PaddingValues(4.dp),
                colors = if (enabledDays[index]) {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                },
                shape = ShapeDefaults.ExtraLarge,
                border = if (enabledDays[index]) {
                    null
                } else {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                }
            ) {
                Text(
                    text = day.uppercase().slice(IntRange(0, 0)),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClickableOutlinedDetails(
    modifier: Modifier = Modifier,
    label: String,
    placeHolder: String,
    value: String,
    leadingIcon: @Composable() (() -> Unit)?,
    onClick: () -> Unit
) {
    OutlinedTextField(
        label = { Text(label) },
        placeholder = { Text(placeHolder) },
        value = value,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        leadingIcon = leadingIcon,
        enabled = false,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = modifier
            .clickable { onClick() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AlarmDetailsPreview() {
    SweckerTheme() {
        val alarm by remember {
            mutableStateOf(
                Alarm(
                    id = "@monesi#1",
                    name = "Alarm test",
                    time = "14:30",
                    date = "mon 7 December",
                    enabledDays = mutableListOf(true, false, false, false, true, true, true),
                    alarmType = AlarmType.PERSONAL
                )
            )
        }
        Scaffold() {
            Box(modifier = Modifier.padding(it)) {
                AlarmDetails(alarm = alarm, isReadOnly = false, onEvent = {})
            }
        }
    }
}