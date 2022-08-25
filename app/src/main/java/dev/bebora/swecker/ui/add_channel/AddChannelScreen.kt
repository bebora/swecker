package dev.bebora.swecker.ui.add_channel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChannelScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {}
) {
    val viewModel: AddChannelViewModel = hiltViewModel()
    val uiState = viewModel.uiState

    Scaffold(
        modifier = modifier,
        topBar = {
            AddChannelTopBar(waitingForServiceResponse = uiState.waitingForServiceResponse, onGoBack = onGoBack)
        },
        floatingActionButton = {
                    if (uiState.channelName.isNotEmpty() && uiState.channelHandle.isNotEmpty()) {
                        FloatingActionButton(
                            modifier = Modifier.imePadding(),
                            onClick = {
                                viewModel.confirmChannelCreation(
                                    onSuccess = {
                                        onGoBack()
                                    }
                                )
                            }) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Finish channel creation"
                            )
                        }
                    }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).imePadding()) {
            AddChannelContent(channelPicUrl = uiState.uploadedPicUrl,
                channelName = uiState.channelName,
                channelHandle = uiState.channelHandle,
                setChannelName = viewModel::setChannelName,
                setChannelPicUrl = viewModel::setChannelPic,
                setChannelHandle = viewModel::setChannelHandle
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChannelTopBar(
    modifier: Modifier = Modifier,
    waitingForServiceResponse: Boolean,
    onGoBack: () -> Unit
) {
    SmallTopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onGoBack) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        title = {
            Text("New channel")

        },
        actions = {
            if (waitingForServiceResponse) {
                CircularProgressIndicator()
            }
        }
    )
}
