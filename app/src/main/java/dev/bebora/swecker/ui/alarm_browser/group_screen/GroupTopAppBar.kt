package dev.bebora.swecker.ui.alarm_browser.group_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.ui.alarm_browser.AlarmBrowserEvent
import dev.bebora.swecker.ui.alarm_browser.DetailsScreenContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTopAppBar(
    modifier: Modifier = Modifier,
    group: Group,
    colors: TopAppBarColors,
    onEvent: (AlarmBrowserEvent) -> Unit,
) {
    SmallTopAppBar(
        colors = colors,
        modifier = modifier.clickable {
            onEvent(AlarmBrowserEvent.DetailsOpened(type = DetailsScreenContent.GROUP_DETAILS))
        },
        title =
        {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = group.name,
                    textAlign = TextAlign.Left,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = true
                )
                Text(
                    modifier = modifier.padding(horizontal = 10.dp),
                    text = group.members.size.toString().plus(" members"),
                    style = MaterialTheme.typography.labelSmall
                )

            }
        },
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onEvent(AlarmBrowserEvent.BackButtonPressed) }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
                Image(
                    painter = ColorPainter(MaterialTheme.colorScheme.tertiary),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        },
        actions = {})
}
