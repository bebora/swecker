package dev.bebora.swecker.ui.contact_browser

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.ui.settings.account.SuggestLogin
import dev.bebora.swecker.util.TestConstants
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
            if (ui.me.id.isBlank() && ui.accountStatusLoaded) {
                SuggestLogin(onNavigate = onNavigate)
            } else {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.friends_section_title),
                    style = MaterialTheme.typography.headlineSmall
                )
                if(ui.friends.isEmpty()){
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp,horizontal = 16.dp),
                        text = stringResource(R.string.no_contact_warning),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
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
                                modifier = Modifier.testTag(TestConstants.removeFriend),
                                imageVector = Icons.Default.PersonRemove,
                                contentDescription = "Remove friend"
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.friendship_requests),
                    style = MaterialTheme.typography.headlineSmall,
                )
                if(ui.friendshipRequests.isEmpty()){
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp,horizontal = 16.dp),
                        text = stringResource(R.string.no_friendship_requests_warning),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                ui.friendshipRequests.forEachIndexed { idx, friend ->
                    if (idx != 0) {
                        Divider()
                    }
                    ContactRow(user = friend) {
                        IconButton(
                            modifier = Modifier.testTag(TestConstants.acceptFriend),
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
            accountsService = FakeAccountsService(
                users = FakeAccountsService.defaultUsers.toMutableMap(),
                friendshipRequests = FakeAccountsService.defaultFriendshipRequests.toMutableMap()
            )
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
    BoxWithConstraints() {
        if (maxWidth > 840.dp) {
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

            Surface(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth(0.6f),
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
                        if (ui.me.id.isBlank() && ui.accountStatusLoaded) {
                            SuggestLogin(onNavigate = onNavigate)
                        } else {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = stringResource(R.string.friends_section_title),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            if(ui.friends.isEmpty()){
                                Text(
                                    modifier = Modifier.padding(vertical = 4.dp,horizontal = 16.dp),
                                    text = stringResource(R.string.no_contact_warning),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
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
                                            modifier = Modifier.testTag(TestConstants.removeFriend),
                                            imageVector = Icons.Default.PersonRemove,
                                            contentDescription = "Remove friend"
                                        )
                                    }
                                }
                            }
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = stringResource(R.string.friendship_requests),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            if(ui.friendshipRequests.isEmpty()){
                                Text(
                                    modifier = Modifier.padding(vertical = 4.dp,horizontal = 16.dp),
                                    text = stringResource(R.string.no_friendship_requests_warning),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            ui.friendshipRequests.forEachIndexed { idx, friend ->
                                if (idx != 0) {
                                    Divider()
                                }
                                ContactRow(user = friend) {
                                    IconButton(
                                        modifier = Modifier.testTag(TestConstants.acceptFriend),
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
        } else {
            ContactBrowserScreen(onNavigate = onNavigate, onGoBack = onGoBack)
        }
    }
}
