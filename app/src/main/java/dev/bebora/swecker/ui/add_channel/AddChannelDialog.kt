package dev.bebora.swecker.ui.add_channel

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.settings.account.SuggestLogin
import dev.bebora.swecker.util.TestConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChannelDialog(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    onNavigate: (String) -> Unit
) {
    val viewModel: AddChannelViewModel = hiltViewModel()
    val uiState = viewModel.uiState

    BoxWithConstraints()
    {
        if (maxWidth > 840.dp) {
            Surface(
                modifier = modifier
                    .fillMaxHeight(0.8f)
                    .fillMaxWidth(0.6f)
                    .imePadding(),
                shape = ShapeDefaults.ExtraLarge
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    if (uiState.me.id.isBlank() && uiState.accountStatusLoaded) {
                        SuggestLogin(onNavigate = onNavigate)
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(4.dp)
                        ) {
                            if (uiState.waitingForServiceResponse) {
                                CircularProgressIndicator()
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.discardChannelCreation(onGoBack) }) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = stringResource(id = R.string.go_back)
                                )
                            }
                        }

                        AddChannelContent(
                            channelName = uiState.channelName,
                            channelHandle = uiState.channelHandle,
                            channelPicUrl = uiState.uploadedPicUrl,
                            setChannelName = viewModel::setChannelName,
                            setChannelPicUrl = viewModel::setChannelPic,
                            setChannelHandle = viewModel::setChannelHandle
                        )


                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            AssistChip(onClick = { viewModel.discardChannelCreation(onGoBack) },
                                label = { Text(stringResource(id = R.string.cancel)) })

                            Spacer(modifier = Modifier.width(8.dp))

                            AssistChip(
                                modifier = Modifier.testTag(TestConstants.confirm),
                                onClick = {
                                    viewModel.confirmChannelCreation(
                                        onSuccess = {
                                            onGoBack()
                                        }
                                    )
                                },
                                label = { Text(stringResource(id = R.string.confirm_dialog)) },
                                enabled = uiState.channelName.isNotEmpty() && uiState.channelHandle.isNotEmpty()
                            )
                        }
                    }
                }
            }
        } else {
            AddChannelScreen(
                onGoBack = onGoBack,
                onNavigate = onNavigate
            )
        }
    }
}
