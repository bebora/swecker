package dev.bebora.swecker.ui.add_group

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.data.User
import dev.bebora.swecker.ui.contact_browser.ContactRow
import dev.bebora.swecker.ui.settings.account.SuggestLogin

@Composable
fun AddGroupContactsList(
    modifier: Modifier = Modifier,
    selectedMembers: List<User>,
    contacts: List<User>,
    searchKey: String,
    onContactPressed: (User) -> Unit,
) {
    val filteredContacts = contacts.filter { contact ->
        contact.username.contains(searchKey, ignoreCase = true) ||
                contact.name.contains(searchKey, ignoreCase = true)
    }

    Divider()

    LazyColumn(
        modifier = modifier
    ) {
        items(filteredContacts) { user ->
            val isUserSelected = selectedMembers.contains(user)

            Surface(
                modifier = Modifier.fillMaxWidth(1f),
                color = if (
                    isUserSelected
                ) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ) {
                ContactRow(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .clickable {
                            onContactPressed(user)
                        }, user = user
                )
            }
            Divider()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupTopBar(
    modifier: Modifier = Modifier,
    memberNum: Int,
    totalContacts: Int,
    content: AddGroupContent,
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
            if (content == AddGroupContent.GROUP_SELECT_CONTACTS) {

                Column() {
                    Text("New group")

                    Text(
                        text = "$memberNum of $totalContacts selected",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text("New group")
            }
        },
        actions = {
            if (waitingForServiceResponse) {
                CircularProgressIndicator()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupInputField(
    modifier: Modifier = Modifier,
    searchKey: String,
    selectedMembers: List<User>,
    onSearchKeyChanged: (String) -> Unit,
    onChipClicked: (User) -> Unit
) {
    val scrollState = ScrollState(0)
    Column() {
        Row(
            modifier = modifier
                .fillMaxWidth(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(4.dp),
                label = { Text("Search contacts") },
                value = searchKey,
                singleLine = true,
                onValueChange = onSearchKeyChanged,
            )
        }
        Row(
            modifier = modifier
                .horizontalScroll(
                    state = scrollState,
                )
                .fillMaxWidth(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            selectedMembers.forEach {
                AddGroupInputChip(user = it, onClick = { user ->
                    onChipClicked(user)
                    onSearchKeyChanged("")
                })
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupInputChip(
    user: User,
    onClick: (User) -> Unit
) {
    InputChip(
        selected = true,
        onClick = {
            onClick(user)
        },
        label = {
            Text(text = user.username)
        },
        trailingIcon = {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Remove selection"
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    onNavigate: (String) -> Unit
) {
    val viewModel: AddGroupViewModel = hiltViewModel()
    val uiState = viewModel.uiState

    Scaffold(
        modifier = modifier,
        topBar = {
            AddGroupTopBar(
                memberNum = uiState.selectedMembers.size,
                totalContacts = uiState.allContacts.size,
                onGoBack = {
                    if (uiState.content == AddGroupContent.GROUP_SELECT_CONTACTS) {
                        viewModel.discardGroupCreation {
                            onGoBack()
                        }
                    } else {
                        viewModel.previousScreen()
                    }
                },
                content = uiState.content,
                waitingForServiceResponse = uiState.waitingForServiceResponse
            )
        },
        floatingActionButton = {
            when (uiState.content) {
                AddGroupContent.GROUP_SELECT_CONTACTS -> {
                    if (uiState.selectedMembers.isNotEmpty()) {
                        FloatingActionButton(onClick = { viewModel.nextScreen() }) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowForward,
                                contentDescription = "Go forward"
                            )
                        }
                    }
                }
                AddGroupContent.GROUP_SELECT_NAME -> {
                    if (uiState.groupName.isNotEmpty()) {
                        FloatingActionButton(
                            onClick = {
                                viewModel.confirmGroupCreation(
                                    onSuccess = {
                                        onGoBack()
                                    }
                                )
                            }) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Finish group creation"
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (uiState.me.id.isBlank() && uiState.accountStatusLoaded) {
                SuggestLogin(onNavigate = onNavigate)
            } else {
                if (uiState.content == AddGroupContent.GROUP_SELECT_CONTACTS) {
                    var searchKey by remember {
                        mutableStateOf("")
                    }
                    Column(modifier = Modifier.fillMaxWidth(1f)) {
                        AddGroupInputField(
                            searchKey = searchKey,
                            selectedMembers = uiState.selectedMembers,
                            onSearchKeyChanged = { newSearchKey -> searchKey = newSearchKey },
                            onChipClicked = viewModel::toggleContactSelection
                        )
                        AddGroupContactsList(
                            selectedMembers = uiState.selectedMembers,
                            contacts = uiState.allContacts,
                            onContactPressed = viewModel::toggleContactSelection,
                            searchKey = searchKey
                        )
                    }

                } else if (uiState.content == AddGroupContent.GROUP_SELECT_NAME) {
                    AddGroupSelectNameScreen(
                        selectedMembers = uiState.selectedMembers,
                        groupName = uiState.groupName,
                        groupPicUrl = uiState.tempGroupData.picture,
                        setGroupName = viewModel::setGroupName,
                        setGroupPicUrl = viewModel::setGroupPic,
                    )
                }
            }
        }
    }
}
