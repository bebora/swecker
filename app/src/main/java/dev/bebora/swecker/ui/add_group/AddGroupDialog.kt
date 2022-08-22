package dev.bebora.swecker.ui.add_group

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupDialog(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {}
) {
    val viewModel: AddGroupViewModel = hiltViewModel()
    val uiState = viewModel.uiState

    Dialog(onDismissRequest = onGoBack) {
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
                        groupPicUrl = uiState.groupPicUrl,
                        setGroupName = viewModel::setGroupName,
                        setGroupPicUrl = viewModel::setGroupPic,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    if (uiState.content == AddGroupContent.GROUP_SELECT_CONTACTS) {
                        Spacer(modifier = Modifier.weight(1f))
                        AssistChip(onClick = onGoBack,
                            label = { Text("Cancel") })

                        Spacer(modifier = Modifier.width(8.dp))

                        AssistChip(
                            onClick = viewModel::nextScreen,
                            label = { Text("Next") },
                            enabled = uiState.selectedMembers.isNotEmpty()
                        )
                    } else if (uiState.content == AddGroupContent.GROUP_SELECT_NAME) {
                        Spacer(modifier = Modifier.weight(1f))
                        AssistChip(onClick = viewModel::previousScreen,
                            label = { Text("Back") })

                        Spacer(modifier = Modifier.width(8.dp))

                        AssistChip(
                            onClick = {
                                viewModel.createGroup()
                                onGoBack()
                            },
                            label = { Text("Ok") },
                            enabled = uiState.groupName.isNotEmpty()
                        )
                    }
                }
            }

        }
    }
}