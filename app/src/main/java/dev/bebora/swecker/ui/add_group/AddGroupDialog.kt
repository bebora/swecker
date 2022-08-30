package dev.bebora.swecker.ui.add_group

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.settings.account.SuggestLogin
import dev.bebora.swecker.util.TestConstants

//TODO display something when the profile picture is being uploaded (similar to what is done in AddGroupScreen)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupDialog(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    onNavigate: (String) -> Unit
) {
    val viewModel: AddGroupViewModel = hiltViewModel()
    val uiState = viewModel.uiState

    BoxWithConstraints {
        if (maxWidth > 840.dp) {
            Surface(
                modifier = modifier
                    .fillMaxHeight(0.8f)
                    .fillMaxWidth(0.6f)
                    .imePadding(),
                shape = ShapeDefaults.ExtraLarge
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
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
                                    onSearchKeyChanged = { newSearchKey ->
                                        searchKey = newSearchKey
                                    },
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

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            if (uiState.content == AddGroupContent.GROUP_SELECT_CONTACTS) {
                                Spacer(modifier = Modifier.weight(1f))
                                AssistChip(
                                    onClick = {
                                        viewModel.discardGroupCreation(onGoBack)
                                    },
                                    label = { Text(stringResource(id = R.string.cancel)) })

                                Spacer(modifier = Modifier.width(8.dp))

                                AssistChip(
                                    modifier = Modifier.testTag(TestConstants.proceed),
                                    onClick = viewModel::nextScreen,
                                    label = { Text(stringResource(R.string.next_group_creation)) },
                                    enabled = uiState.selectedMembers.isNotEmpty()
                                )
                            } else if (uiState.content == AddGroupContent.GROUP_SELECT_NAME) {
                                Spacer(modifier = Modifier.weight(1f))
                                AssistChip(
                                    onClick = viewModel::previousScreen,
                                    label = { Text(stringResource(R.string.back_group_creation)) })

                                Spacer(modifier = Modifier.width(8.dp))

                                AssistChip(
                                    modifier = Modifier.testTag(TestConstants.confirm),
                                    onClick = {
                                        viewModel.confirmGroupCreation(
                                            onSuccess = {
                                                onGoBack()
                                            }
                                        )
                                    },
                                    label = { Text(stringResource(id = R.string.confirm_dialog)) },
                                    enabled = uiState.groupName.isNotEmpty()
                                )
                            }
                        }
                    }
                }
            }
        }
        else{
            AddGroupScreen(
                onGoBack = onGoBack,
                onNavigate = onNavigate
            )
        }
    }
}
