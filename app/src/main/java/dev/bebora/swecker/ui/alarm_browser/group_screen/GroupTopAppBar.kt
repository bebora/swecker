package dev.bebora.swecker.ui.alarm_browser.group_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.ui.settings.account.PropicPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTopAppBar(
    modifier: Modifier = Modifier,
    group: Group,
    colors: TopAppBarColors,
    onGoBack: () -> Unit = {}
) {
    SmallTopAppBar(
        colors = colors,
        modifier = modifier,
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
                IconButton(onClick = onGoBack) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
                SubcomposeAsyncImage(
                    model = group.groupPicUrl,
                    loading = {
                        PropicPlaceholder(
                            size = 45.dp,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        {
                            CircularProgressIndicator()
                        }
                    },
                    error = {
                        PropicPlaceholder(
                            size = 45.dp,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                        }
                    },
                    contentDescription = "Group profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .requiredSize(45.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        },
        actions = {})
}
