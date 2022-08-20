package dev.bebora.swecker.ui.contact_browser.add_contact

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.Search
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
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.impl.AccountsServiceImpl
import dev.bebora.swecker.data.service.impl.AuthServiceImpl
import dev.bebora.swecker.ui.contact_browser.ContactRow
import dev.bebora.swecker.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    viewModel: AddContactViewModel = hiltViewModel(),
) {
    val ui = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

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
                    if (ui.uploadingFriendshipRequest) {
                        CircularProgressIndicator()
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(8.dp)
                .verticalScroll(state = rememberScrollState()),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(R.string.search_contact_placeholder))
                },
                value = ui.currentQuery,
                onValueChange = {
                    viewModel.onEvent(AddContactEvent.QueueSearch(it))
                },
                singleLine = true,
                maxLines = 1,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search contact icon"
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AlternateEmail,
                        contentDescription = "Search contact icon"
                    )
                },

                )
            ui.queryResults.forEachIndexed { idx, friend ->
                if (idx != 0) {
                    Divider()
                }
                ContactRow(user = friend, trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                viewModel.onEvent(AddContactEvent.SendFriendshipRequest(to = friend))
                            },
                        imageVector = Icons.Outlined.PersonAddAlt,
                        contentDescription = "Send friendship request to ${friend.name}"
                    )
                })
            }
        }
    }
}

@Preview
@Composable
fun AddContactScreenPreview() {
    AddContactScreen(
        viewModel = AddContactViewModel(
            authService = AuthServiceImpl(),
            accountsService = AccountsServiceImpl()
        )
    )
}
