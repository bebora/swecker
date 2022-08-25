package dev.bebora.swecker.ui.alarm_browser.group_screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.ui.theme.SweckerTheme
import java.time.OffsetDateTime

@Composable
fun GroupList(
    modifier: Modifier = Modifier,
    groups: List<Group>,
    selectedGroupId: String? = null,
    onEvent: (Group) -> Unit,
) {
    LazyColumn() {
        items(groups) { group ->
            GroupItem(
                modifier = modifier,
                group = group,
                selected = selectedGroupId == group.id,
                onEvent = onEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GroupListPreview() {
    SweckerTheme() {
        Scaffold() {
            GroupList(
                modifier = Modifier.padding(it),
                groups = listOf(
                    Group(
                        "1",
                        "Wanda the group",
                        members = emptyList(),
                        firstAlarmName = "An alarm!",
                        firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
                        owner = "@me"
                    ),
                    Group(
                        "2",
                        "Another group",
                        members = emptyList(),
                        firstAlarmName = "An alarm!",
                        firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
                        owner = "@you"
                    ),
                    Group(
                        "3",
                        "A third group! Very long title",
                        members = emptyList(),
                        firstAlarmName = "An alarm!",
                        firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
                        owner = "@you"
                    ),
                ),
                selectedGroupId = "3",
                onEvent = {})
        }
    }
}
