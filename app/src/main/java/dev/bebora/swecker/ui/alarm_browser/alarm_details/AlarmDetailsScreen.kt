package dev.bebora.swecker.ui.alarm_browser.alarm_details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserUIState
import dev.bebora.swecker.ui.theme.SweckerTheme
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDetails(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    canDelete: Boolean,
    onAlarmPartiallyUpdated: (Alarm) -> Unit,
    onUpdateCompleted: (Alarm, Boolean) -> Unit,
    onAlarmDeleted: (Alarm) -> Unit = {}
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    val enabledRepetition = alarm.enabledDays.reduceRight { a, b -> a || b }

    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.imePadding()) {
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
                    value = alarm.localTime!!.format(DateTimeFormatter.ofPattern("hh:mm a")),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null
                        )
                    },
                    onClick = { showTimePicker = true }
                )

                if (!enabledRepetition) {
                    ClickableOutlinedDetails(
                        modifier = Modifier
                            .weight(0.5f),
                        label = "Date",
                        placeHolder = "Select date",
                        value = alarm.localDate!!.format(
                            DateTimeFormatter.ofLocalizedDate(
                                FormatStyle.SHORT
                            )
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.EditCalendar,
                                contentDescription = null
                            )
                        },
                        onClick = { showDatePicker = true }
                    )
                }
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
                    onAlarmPartiallyUpdated(alarm.copy(name = newName))
                },
                readOnly = false,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(1f),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                ),
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
                    onAlarmPartiallyUpdated(

                        alarm.copy(
                            enabledDays = alarm.enabledDays.toMutableList()
                                .also { it[index] = enabled })
                    )

                })
            Spacer(modifier = Modifier.height(4.dp))
            Divider()

            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .clickable {
                        onAlarmPartiallyUpdated(alarm.copy(enableChat = !alarm.enableChat))
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
                        onAlarmPartiallyUpdated(
                            alarm.copy(
                                enableChat = !alarm.enableChat
                            )

                        )
                    },
                ) {
                    if (alarm.enableChat) {
                        Icon(imageVector = Icons.Outlined.Check, contentDescription = null)
                    }
                }
            }
            if (canDelete) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(1f),
                    onClick = { showDeleteAlert = true }) {
                    Text(text = "Delete alarm")
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
                        onUpdateCompleted(

                            alarm,
                            false

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
                        val date = if (!enabledRepetition) {
                            alarm.localDate
                        } else {
                            nextEnabledDate(
                                enabledDays = alarm.enabledDays,
                                time = alarm.localTime!!
                            ).toLocalDate()
                        }
                        onUpdateCompleted(
                            alarm.copy(
                                localDate = date,
                                dateTime = ZonedDateTime.of(
                                    date,
                                    alarm.localTime,
                                    ZoneId.systemDefault()
                                ).toOffsetDateTime()
                            ),
                            true

                        )
                    })
                ) {
                    Text("Ok")
                }
            }

        }
        if (showDatePicker)
            DatePicker(onDateSelected = { newDate ->
                onAlarmPartiallyUpdated(
                    alarm.copy(
                        localDate = newDate
                    )
                )
            }, onDismissRequest = {
                showDatePicker = false
            })
        if (showTimePicker)
            TimePicker(onTimeSelected = { newTime ->
                onAlarmPartiallyUpdated(
                    alarm.copy(
                        localTime = newTime
                    )
                )
            }, onDismissRequest = {
                showTimePicker = false
            })
        if (showDeleteAlert) {
            AlertDialog(onDismissRequest = { showDeleteAlert = false },
                title = { Text(text = "Delete alarm") },
                text = { Text(text = "Do you really want to delete this alarm? The operation is irreversible") },
                confirmButton = {
                    OutlinedButton(onClick = { onAlarmDeleted(alarm) }) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteAlert = false }) {
                        Text(text = "No")
                    }
                })
        }
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
        val days =
            DayOfWeek.values().map { d -> d.getDisplayName(TextStyle.NARROW, Locale.getDefault()) }
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
                    text = day.uppercase(),
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
        var alarm by remember {
            mutableStateOf(
                Alarm(
                    id = "@monesi#1",
                    name = "Alarm test",
                    enabledDays = mutableListOf(true, false, false, false, true, true, true),
                    alarmType = AlarmType.PERSONAL,
                    localTime = LocalTime.now()
                )
            )
        }
        Scaffold() {
            Box(modifier = Modifier.padding(it)) {
                AlarmDetails(
                    alarm = alarm,
                    canDelete = false,
                    onAlarmPartiallyUpdated = { al -> alarm = al },
                    onUpdateCompleted = { _, _ -> })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDetailsScreen(
    modifier: Modifier = Modifier,
    onEvent: (AlarmBrowserEvent) -> Unit,
    uiState: AlarmBrowserUIState,
    roundTopCorners: Boolean
) {
    Scaffold(
        topBar = {
            Surface(
                shape = if (roundTopCorners) {
                    RoundedCornerShape(
                        topEnd = 20.dp,
                        topStart = 20.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                } else {
                    RectangleShape
                }
            ) {
                SmallTopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = modifier,
                    title = { Text(text = "Alarm details", textAlign = TextAlign.Center) },
                    navigationIcon = {
                        IconButton(onClick = { onEvent(AlarmBrowserEvent.BackButtonPressed) }) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = "Go back"
                            )
                        }
                    },
                    actions = {
                    })
            }
        }
    ) {
        val myId = uiState.me.id
        val selectedAlarm = uiState.selectedAlarm
        val canDeleteAlarm = uiState.channels.firstOrNull { channel ->
            channel.id == selectedAlarm?.groupId
        }?.owner == myId || uiState.groups.firstOrNull { group ->
            group.id == selectedAlarm?.groupId
        }?.owner == myId || uiState.selectedAlarm?.alarmType == AlarmType.PERSONAL

        AlarmDetails(
            modifier = modifier.padding(it),
            alarm = uiState.selectedAlarm!!,
            canDelete = canDeleteAlarm,
            onAlarmPartiallyUpdated = { al ->
                onEvent(
                    AlarmBrowserEvent.AlarmPartiallyUpdated(
                        al
                    )
                )
            },
            onUpdateCompleted = { al, b -> onEvent(AlarmBrowserEvent.AlarmUpdated(al, b)) },
            onAlarmDeleted = { al -> onEvent(AlarmBrowserEvent.AlarmDeleted(al)) },
        )
    }

}
