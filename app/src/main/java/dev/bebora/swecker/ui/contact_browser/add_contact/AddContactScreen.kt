package dev.bebora.swecker.ui.contact_browser.add_contact

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.impl.AccountsServiceImpl
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddContactScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: AddContactViewModel = hiltViewModel(),
) {
    val ui = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = true) {
        viewModel.addContactUiEvent.collect { event ->
            Log.d("SWECKER-Received", "received an event")
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.uiText.asString(context = context),
                    )
                }
                else -> Unit
            }
        }
    }

    //TODO handle larger screen
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(R.string.add_contact_title)) },
                navigationIcon = {
                    IconButton(onClick = { onGoBack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    if (ui.uploadingFriendshipRequest || ui.processingQuery) {
                        CircularProgressIndicator()
                    }
                }
            )
        },
    ) { padding ->
        AddContactContent(
            modifier = Modifier.padding(padding),
            keyboardController = keyboardController,
            ui = ui,
            onNavigate = onNavigate,
            onEvent = viewModel::onEvent
        )
    }
}

@Preview
@Composable
fun AddContactScreenPreview() {
    AddContactScreen(
        viewModel = AddContactViewModel(
            authService = FakeAuthService(),
            accountsService = AccountsServiceImpl()
        )
    )
}
