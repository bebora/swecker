package dev.bebora.swecker.ui.alarm_browser.group_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.theme.SweckerTheme
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

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
            members = emptyList(),
            owner = "@me"
        ),
            onEvent = {})
    }
}
