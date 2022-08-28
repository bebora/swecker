package dev.bebora.swecker.ui.contact_browser.add_contact

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.contact_browser.ContactRow
import dev.bebora.swecker.ui.settings.account.SuggestLogin

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddContactContent(
    modifier: Modifier = Modifier,
    keyboardController: SoftwareKeyboardController?,
    ui: AddContactUiState,
    onNavigate: (String) -> Unit,
    onEvent: (AddContactEvent) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                keyboardController?.hide()
            },
    ) {
        if (ui.me.id.isBlank() && ui.accountStatusLoaded) {
            SuggestLogin(onNavigate = onNavigate)
        } else {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(R.string.search_contact_by_username_placeholder))
                },
                value = ui.currentQuery,
                onValueChange = {
                    onEvent(AddContactEvent.QueueSearch(it))
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
            val showableResults = ui.queryResults
                .filter { !ui.friendsIds.contains(it.id) }
            if (showableResults.isEmpty() && !ui.processingQuery && ui.currentQuery.isNotBlank()) {
                // TODO improve design
                Text(
                    text = stringResource(R.string.no_users_found),
                    style = MaterialTheme.typography.titleSmall
                )
            } else {
                showableResults.forEachIndexed { idx, friend ->
                    if (idx != 0) {
                        Divider()
                    }
                    ContactRow(user = friend, trailingIcon = {
                        IconButton(
                            onClick = {
                                onEvent(AddContactEvent.SendFriendshipRequest(to = friend))
                            }) {
                            Icon(
                                imageVector = Icons.Outlined.PersonAddAlt,
                                contentDescription = "Send friendship request to ${friend.name}"
                            )
                        }
                    })
                }
            }
        }
    }
}
