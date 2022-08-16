package dev.bebora.swecker.ui.settings.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.LOGIN
import dev.bebora.swecker.R
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.settings.Settings
import dev.bebora.swecker.ui.settings.*
import dev.bebora.swecker.ui.theme.SweckerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDummyScreen(
    settings: Settings,
    ui: SettingsUiState,
    modifier: Modifier = Modifier,
    onEvent: (SettingsEvent) -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    //TODO manage profile picture
    val sections = listOf(
        SettingsSection(
            title = ui.savedName,
            stringResource(R.string.account_change_name),
            Icons.Outlined.Person,
            onClick = { onEvent(SettingsEvent.OpenEditName) }
        ),
        SettingsSection(
            ui.savedUsername,
            stringResource(R.string.account_change_username),
            Icons.Outlined.AlternateEmail,
            onClick = { onEvent(SettingsEvent.OpenEditUsername) }
        )
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(R.string.account_section_title)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(SettingsEvent.CloseSettingsSubsection) }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .requiredSize(160.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.error
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(80.dp)
                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Registered to Firebase: " + ui.hasUser.toString(), color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = ui.userId, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "current name: ${ui.currentName}", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "current username: ${ui.currentUsername}", color = MaterialTheme.colorScheme.error)
            sections.forEach { section ->
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
                SettingsItem(
                    title = section.title,
                    description = section.description ?: "Default description",
                    icon = section.icon,
                    onClick = section.onClick
                )
            }
            Button(onClick = { onNavigate(LOGIN) }) {
                Text(text = stringResource(id = R.string.log_in_button))
            }
        }
    }
    if (ui.showEditNamePopup) {
        AlertDialog(
            title = {
                Text(text = "Edit name")
            },
            onDismissRequest = { onEvent(SettingsEvent.DismissEditName) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(SettingsEvent.SaveUser(
                            User(
                                id = ui.userId,
                                name = ui.currentName,
                                username = ui.savedUsername
                            )))
                    }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onEvent(SettingsEvent.DismissEditName)
                    }) {
                    Text(text = "Dismiss")
                }
            },
            text = {
                OutlinedTextField(
                    value = ui.currentName,
                    onValueChange = { onEvent(SettingsEvent.SetTempName(it)) })
            }
        )
    }
    if (ui.showEditUsernamePopup) {
        AlertDialog(
            title = {
                Text(text = "Edit username")
            },
            onDismissRequest = { onEvent(SettingsEvent.DismissEditUsername) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(SettingsEvent.SaveUser(
                            User(
                                id = ui.userId,
                                name = ui.savedName,
                                username = ui.currentUsername
                            )))
                    }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onEvent(SettingsEvent.DismissEditUsername)
                    }) {
                    Text(text = "Dismiss")
                }
            },
            text = {
                OutlinedTextField(
                    value = ui.currentUsername,
                    onValueChange = { onEvent(SettingsEvent.SetTempUsername(it)) })
            }
        )
    }
}

@Preview(locale = "en")
@Composable
fun AccountDummyScreenPreview() {
    SweckerTheme {
        AccountDummyScreen(
            settings = Settings(),
            ui = SettingsUiState(),
            onEvent = {}
        )
    }
}
