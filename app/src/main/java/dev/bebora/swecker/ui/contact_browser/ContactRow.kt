package dev.bebora.swecker.ui.contact_browser

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import dev.bebora.swecker.data.User
import dev.bebora.swecker.ui.settings.account.PropicPlaceholder

@Composable
fun ContactRow(
    modifier: Modifier = Modifier,
    user: User,
    trailingIcon: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SubcomposeAsyncImage(
            model = user.propicUrl,
            loading = {
                PropicPlaceholder(
                    size = 60.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                {
                    CircularProgressIndicator()
                }
            },
            error = {
                PropicPlaceholder(
                    size = 60.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                }
            },
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .requiredSize(60.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
        )
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        trailingIcon()
    }
}

@Preview(showBackground = true)
@Composable
fun ContactRowPreview() {
    ContactRow(
        user = User(
            id = "fake",
            username = "fakeusername",
            name = "fakename",
            propicUrl = "https://www.w3schools.com/html/img_girl.jpg"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ContactRowNoPropicPreview() {
    ContactRow(
        user = User(
            id = "fake",
            username = "fakeusername",
            name = "fakename",
            propicUrl = ""
        )
    )
}
