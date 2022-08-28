package dev.bebora.swecker.ui.add_channel

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.google.modernstorage.photopicker.PhotoPicker
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.settings.account.PropicPlaceholder
import dev.bebora.swecker.util.TestConstants

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChannelContent(
    modifier: Modifier = Modifier,
    channelPicUrl: String,
    channelName: String,
    channelHandle: String,
    setChannelName: (String) -> Unit = {},
    setChannelPicUrl: (Uri) -> Unit = {},
    setChannelHandle: (String) -> Unit = {}
) {
    val photoPicker = rememberLauncherForActivityResult(PhotoPicker()) { uris ->
        if (uris.isNotEmpty()) {
            val imageUri = uris[0]
            setChannelPicUrl(imageUri)
        }
    }


    Row(
        modifier = modifier
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        if (channelPicUrl.isNotEmpty()) {
            SubcomposeAsyncImage(
                model = channelPicUrl,
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
                    .clickable {
                        photoPicker.launch(
                            PhotoPicker.Args(
                                PhotoPicker.Type.IMAGES_ONLY,
                                1
                            )
                        )
                    }
            )
        } else {
            FilledIconButton(
                modifier = Modifier.requiredSize(60.dp),
                onClick = {
                    photoPicker.launch(
                        PhotoPicker.Args(
                            PhotoPicker.Type.IMAGES_ONLY,
                            1
                        )
                    )
                }) {
                Icon(
                    imageVector = Icons.Outlined.AddPhotoAlternate,
                    contentDescription = "Add profile picture"
                )
            }
        }

        Column(modifier = modifier) {
            OutlinedTextField(
                modifier = Modifier
                    .testTag(TestConstants.name)
                    .fillMaxWidth(1f)
                    .imePadding()
                    .padding(horizontal = 16.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Label, contentDescription = null)
                },
                label = { Text(stringResource(R.string.channel_name)) },
                maxLines = 1,
                value = channelName,
                onValueChange = setChannelName,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.AlternateEmail, contentDescription = null)
                },
                modifier = Modifier
                    .testTag(TestConstants.handle)
                    .fillMaxWidth(1f)
                    .imePadding()
                    .padding(horizontal = 16.dp),
                maxLines = 1,
                label = { Text(stringResource(R.string.channel_handle)) },
                value = channelHandle,
                onValueChange = setChannelHandle,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

    }


}
