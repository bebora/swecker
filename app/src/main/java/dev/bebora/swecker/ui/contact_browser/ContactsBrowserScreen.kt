package dev.bebora.swecker.ui.contact_browser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.impl.AccountsServiceImpl
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.ui.settings.account.SuggestLogin
import dev.bebora.swecker.util.UiEvent

// TODO This screen and the corresponding dialog have a lot in common, the content should be a common composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactBrowserScreen(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit,
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
                scrollBehavior = scrollBehavior,
                actions = {
                    if (ui.uploadingFriendshipRequest) {
                        CircularProgressIndicator()
                    }
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(state = rememberScrollState()),
        ) {
            if (ui.me.id.isBlank()) {
                SuggestLogin(onNavigate = onNavigate)
            } else {
                ui.friends.forEachIndexed { idx, friend ->
                    if (idx != 0) {
                        Divider()
                    }
                    ContactRow(user = friend) {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(
                                    ContactsEvent.RemoveFriend(
                                        friend = friend
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonRemove,
                                contentDescription = "Remove friend"
                            )
                        }
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
                    ContactRow(user = friend) {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(
                                    ContactsEvent.AcceptFriendshipRequest(
                                        from = friend
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add friend"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ContactBrowserScreenPreview() {
    ContactBrowserScreen(
        viewModel = ContactsBrowserViewModel(
            authService = FakeAuthService(),
            accountsService = AccountsServiceImpl()
        ),
        onNavigate = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactBrowserDialog(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    onNavigate: (String) -> Unit,
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

    Dialog(onDismissRequest = { onGoBack() }) {
        Surface(
            modifier = Modifier.fillMaxHeight(0.9f),
            shape = ShapeDefaults.ExtraLarge,
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                modifier = modifier
                    .padding(16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    SmallTopAppBar(
                        title = { Text(text = stringResource(R.string.contacts_title)) },
                        actions = {
                            if (ui.uploadingFriendshipRequest) {
                                CircularProgressIndicator()
                            }
                            IconButton(onClick = { onGoBack() }) {
                                Icon(
                                    Icons.Filled.Close,
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
                    if (ui.me.id.isBlank()) {
                        SuggestLogin(onNavigate = onNavigate)
                    } else {
                        ui.friends.forEachIndexed { idx, friend ->
                            if (idx != 0) {
                                Divider()
                            }
                            ContactRow(user = friend) {
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            ContactsEvent.RemoveFriend(
                                                friend = friend
                                            )
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonRemove,
                                        contentDescription = "Remove friend"
                                    )
                                }
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
                            ContactRow(user = friend) {
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            ContactsEvent.AcceptFriendshipRequest(
                                                from = friend
                                            )
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add friend"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
