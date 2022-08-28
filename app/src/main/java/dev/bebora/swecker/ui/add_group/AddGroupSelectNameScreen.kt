package dev.bebora.swecker.ui.add_group

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.google.modernstorage.photopicker.PhotoPicker
import dev.bebora.swecker.data.User
import dev.bebora.swecker.ui.settings.account.PropicPlaceholder
import dev.bebora.swecker.util.TestConstants

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupSelectNameScreen(
    modifier: Modifier = Modifier,
    selectedMembers: List<User>,
    groupPicUrl: String,
    groupName: String,
    setGroupName: (String) -> Unit = {},
    setGroupPicUrl: (Uri) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val photoPicker = rememberLauncherForActivityResult(PhotoPicker()) { uris ->
        if (uris.isNotEmpty()) {
            val imageUri = uris[0]
            setGroupPicUrl(imageUri)
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(80.dp)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (groupPicUrl.isNotEmpty()) {
                SubcomposeAsyncImage(
                    model = groupPicUrl,
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

            OutlinedTextField(
                modifier = Modifier
                    .testTag(TestConstants.name)
                    .weight(1f),
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Label, contentDescription = null)
                },
                maxLines = 1,
                singleLine = true,
                label = { Text("Name") },
                value = groupName,
                onValueChange = setGroupName,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                ),
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = "${selectedMembers.size} members",
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.height(4.dp))
        AddGroupContactsList(
            selectedMembers = emptyList(),
            contacts = selectedMembers,
            searchKey = "",
            onContactPressed = {}
        )
    }


}
