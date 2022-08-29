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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAlarmProviderService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.data.service.testimpl.FakeImageStorageService
import dev.bebora.swecker.ui.settings.account.SuggestLogin
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.util.TestConstants
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChannelScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    viewModel: AddChannelViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    val uiState = viewModel.uiState

    Scaffold(
        modifier = modifier,
        topBar = {
            AddChannelTopBar(
                waitingForServiceResponse = uiState.waitingForServiceResponse,
                onGoBack = onGoBack
            )
        },
        floatingActionButton = {
            if (uiState.channelName.isNotEmpty() && uiState.channelHandle.isNotEmpty()) {
                FloatingActionButton(
                    modifier = Modifier
                        .imePadding()
                        .testTag(TestConstants.confirm),
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
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .imePadding()
        ) {
            if (uiState.me.id.isBlank() && uiState.accountStatusLoaded) {
                SuggestLogin(onNavigate = onNavigate)
            } else {
                AddChannelContent(
                    channelPicUrl = uiState.uploadedPicUrl,
                    channelName = uiState.channelName,
                    channelHandle = uiState.channelHandle,
                    setChannelName = viewModel::setChannelName,
                    setChannelPicUrl = viewModel::setChannelPic,
                    setChannelHandle = viewModel::setChannelHandle
                )
            }
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

@Preview
@Composable
fun AddChannelScreenPreview() {
    SweckerTheme {
        AddChannelScreen(
            onNavigate = {},
            viewModel = AddChannelViewModel(
                authService = FakeAuthService(),
                accountsService = FakeAccountsService(
                    users = FakeAccountsService.defaultUsers.toMutableMap(),
                    friendshipRequests = FakeAccountsService.defaultFriendshipRequests.toMutableMap()
                ),
                imageStorageService = FakeImageStorageService(),
                alarmProviderService = FakeAlarmProviderService(),
                iODispatcher = Dispatchers.IO,
            )
        )
    }
}
