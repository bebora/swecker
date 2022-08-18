package dev.bebora.swecker.ui.alarm_browser


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAlarm
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.alarmTypeToIcon
import dev.bebora.swecker.ui.theme.SweckerTheme
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmCard(
    alarm: Alarm,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onEvent: (AlarmBrowserEvent) -> Unit = {}
) {
    Card(
        onClick = { onEvent(AlarmBrowserEvent.AlarmSelected(alarm)) },
        enabled = alarm.enabled,
        modifier = modifier
            .heightIn(80.dp, 130.dp),
        elevation = if (selected) CardDefaults.cardElevation(
            defaultElevation = 16.dp
        ) else CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(1.0f)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(1.0f)
            ) {
                Text(
                    style = MaterialTheme.typography.displayLarge,
                    text = alarm.localTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    style = MaterialTheme.typography.displaySmall,
                    text = alarm.localTime?.format(DateTimeFormatter.ofPattern("a")) ?: ""
                )

                Spacer(Modifier.weight(1f))

                Switch(
                    checked = alarm.enabled,
                    onCheckedChange = {
                        onEvent(
                            AlarmBrowserEvent.AlarmUpdated(
                                alarm = alarm.copy(
                                    enabled = !alarm.enabled
                                ),
                                success = true
                            )
                        )
                    },
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
                    text = alarm.localDate?.format(DateTimeFormatter.ofPattern("eee, dd MMM uuuu"))
                        ?: "",
                    modifier = Modifier.width(120.dp)
                )
                Icon(
                    modifier = Modifier.requiredSize(30.dp),
                    imageVector = alarmTypeToIcon(alarm.alarmType, alarm.enabled),
                    contentDescription = alarm.alarmType.toString(),
                    tint = if (alarm.enabled) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
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

@Composable
fun GroupItem(
    modifier: Modifier = Modifier,
    group: Group,
    selected: Boolean = false,
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    Row(modifier = modifier
        .fillMaxWidth(1f)
        .background(
            if (!selected) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
        .clickable { onEvent(AlarmBrowserEvent.GroupSelected(group)) }
        .padding(all = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = ColorPainter(MaterialTheme.colorScheme.tertiary),
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = group.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.headlineMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Row(
                modifier = Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = group.firstAlarmName,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(all = 4.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = group.firstAlarmDateTime!!.format(DateTimeFormatter.ofPattern("eee\n dd MMM uuuu")),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .width(80.dp),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GroupItemPreview() {
    SweckerTheme() {
        GroupItem(group = Group(
            "1",
            "Wanda the group",
            firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
            members = null,
            owner = "@me"
        ),
            onEvent = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmBrowserSearchBar(
    modifier: Modifier = Modifier,
    searchKey: String,
    placeHolderString: String = "",
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(1f),
        value = searchKey,
        onValueChange = { searchValue -> onEvent(AlarmBrowserEvent.Search(searchValue)) },
        placeholder = { Text(placeHolderString) },
        leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
        shape = ShapeDefaults.ExtraLarge,
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun SweckerFab(
    modifier: Modifier = Modifier,
    destination: NavBarDestination,
    onClick: () -> Unit
) {
    FloatingActionButton(modifier = modifier, onClick = { onClick() }) {
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
