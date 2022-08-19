package dev.bebora.swecker.ui.contact_browser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.impl.AccountsServiceImpl
import dev.bebora.swecker.data.service.impl.AuthServiceImpl
import dev.bebora.swecker.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactBrowserScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    viewModel: ContactsBrowserViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val ui = viewModel.uiState
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.contactsUiEvent.collect { event ->
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
                title = { Text(text = stringResource(R.string.contacts_title)) },
                navigationIcon = {
                    IconButton(onClick = { onGoBack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(state = rememberScrollState()),
        ) {
            ui.friends.forEachIndexed { idx, friend ->
                if (idx != 0) {
                    Divider()
                }
                ContactRow(user = friend)
            }
            if (ui.me.id.isNotBlank()) {
                Button(onClick = {
                    viewModel.onEvent(ContactsEvent.RequestFriendship(ui.me, ui.me))
                }) {
                    Text("Create dummy friendship request")
                }
            }
            Text(
                text = "Friendship requests",
                style = MaterialTheme.typography.headlineSmall
            )
            ui.friendshipRequests.forEachIndexed { idx, friend ->
                if (idx != 0) {
                    Divider()
                }
                ContactRow(user = friend)
            }
        }
    }
}

@Preview
@Composable
fun ContactBrowserScreenPreview() {
    ContactBrowserScreen(
        viewModel = ContactsBrowserViewModel(
            authService = AuthServiceImpl(),
            accountsService = AccountsServiceImpl()
        )
    )
}

