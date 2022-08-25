package dev.bebora.swecker.ui.add_channel

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChannelDialog(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {}
) {
    val viewModel: AddChannelViewModel = hiltViewModel()
    val uiState = viewModel.uiState

    Dialog(onDismissRequest = {
        viewModel.discardChannelCreation(onGoBack)
    }) {
        Surface(
            modifier = modifier
                .fillMaxHeight(0.9f)
                .imePadding(),
            shape = ShapeDefaults.ExtraLarge
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {

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
                        label = { Text("Cancel") })

                    Spacer(modifier = Modifier.width(8.dp))

                    AssistChip(
                        onClick = {
                            viewModel.confirmChannelCreation(
                                onSuccess = {
                                    onGoBack()
                                }
                            )
                        },
                        label = { Text("Ok") },
                        enabled = uiState.channelName.isNotEmpty() && uiState.channelHandle.isNotEmpty()
                    )
                }
            }
        }
    }
}
