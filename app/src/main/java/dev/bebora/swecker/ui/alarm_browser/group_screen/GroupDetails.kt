package dev.bebora.swecker.ui.alarm_browser.group_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.User
import dev.bebora.swecker.ui.contact_browser.ContactRow
import dev.bebora.swecker.ui.theme.SweckerTheme
import java.time.OffsetDateTime

@Composable
fun GroupDetails(
    modifier: Modifier = Modifier,
    group: Group,
    usersData: Map<String, User> = emptyMap(),
) {
    LazyColumn(
        modifier = modifier
    ) {

        items(group.members) { userId ->
            val user = usersData[userId]
            if(user != null) {
                ContactRow(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .clickable {
                        }, user = user
                )
            }else{
                Row(horizontalArrangement = Arrangement.Center,
                modifier = Modifier.height(120.dp)) {
                    CircularProgressIndicator()
                }
            }
            Divider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GroupDetails() {
    SweckerTheme() {
        Scaffold(
            topBar = {
                GroupTopAppBar(group = Group(
                    "1",
                    "Wanda the group",
                    firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
                    members = emptyList(),
                    owner = "@me"
                ), colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ))
            }
        ) {
            Box(modifier = Modifier.padding(it)) {
                GroupDetails(
                    group = Group(
                        "1",
                        "Wanda the group",
                        firstAlarmDateTime = OffsetDateTime.parse("2011-12-03T10:15:30+02:00"),
                        members = emptyList(),
                        owner = "@me"
                    )
                )
            }
        }
    }
}
