package dev.bebora.swecker.ui.settings.account

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import dev.bebora.swecker.util.LOGIN
import dev.bebora.swecker.R
import dev.bebora.swecker.data.User
import dev.bebora.swecker.ui.settings.*
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.util.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDummyScreen(
    ui: SettingsUiState,
    modifier: Modifier = Modifier,
    onEvent: (SettingsEvent) -> Unit,
    uiEvent: Flow<UiEvent> = emptyFlow(),
    onNavigate: (String) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        uiEvent.collect { event ->
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
                title = { Text(text = stringResource(R.string.account_section_title)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(SettingsEvent.CloseSettingsSubsection) }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    if (ui.accontLoading || ui.savedName.isBlank()) {
                        CircularProgressIndicator()
                    }
                }
            )
        },
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (!ui.hasUser) {
                SuggestLogin(onNavigate = onNavigate)
            } else {
                //TODO manage profile picture
                val sections = listOf(
                    SettingsSection(
                        title = ui.savedName,
                        stringResource(R.string.account_change_name),
                        Icons.Outlined.Person,
                        onClick = { onEvent(SettingsEvent.OpenEditName) }
                    ),
                    SettingsSection(
                        "@${ui.savedUsername}",
                        stringResource(R.string.account_change_username),
                        Icons.Outlined.AlternateEmail,
                        onClick = { onEvent(SettingsEvent.OpenEditUsername) }
                    )
                )

                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    SubcomposeAsyncImage(
                        model = ui.propicUrl,
                        loading = {
                            PropicPlaceholder()
                            {
                                CircularProgressIndicator()
                            }
                        },
                        error = {
                            PropicPlaceholder {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Error getting profile picture",
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .background(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = CircleShape
                                        )
                                        .padding(8.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        contentDescription = "Profile picture",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .requiredSize(160.dp)
                            .clip(CircleShape)
                    )
                    IconButton(
                        modifier = Modifier.background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                        onClick = { /*TODO add image uploading*/ }) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Add new profile picture",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                sections.forEach { section ->
                    Divider()
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
    }
    if (ui.showEditNamePopup) {
        AlertDialog(
            title = {
                Text(text = stringResource(R.string.edit_name))
            },
            onDismissRequest = { onEvent(SettingsEvent.DismissEditName) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(
                            SettingsEvent.SaveUser(
                                User(
                                    id = ui.userId,
                                    name = ui.currentName.trim(),
                                    username = ui.savedUsername.trim()
                                )
                            )
                        )
                    }) {
                    Text(text = stringResource(id = R.string.confirm_dialog))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onEvent(SettingsEvent.DismissEditName)
                    }) {
                    Text(text = stringResource(id = R.string.dismiss_dialog))
                }
            },
            text = {
                OutlinedTextField(
                    value = ui.currentName,
                    onValueChange = { onEvent(SettingsEvent.SetTempName(it)) },
                    singleLine = true
                )
            }
        )
    }
    if (ui.showEditUsernamePopup) {
        AlertDialog(
            title = {
                Text(text = stringResource(R.string.edit_username))
            },
            onDismissRequest = { onEvent(SettingsEvent.DismissEditUsername) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(
                            SettingsEvent.SaveUser(
                                User(
                                    id = ui.userId,
                                    name = ui.savedName.trim(),
                                    username = ui.currentUsername.trim()
                                )
                            )
                        )
                    }) {
                    Text(text = stringResource(id = R.string.confirm_dialog))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onEvent(SettingsEvent.DismissEditUsername)
                    }) {
                    Text(text = stringResource(id = R.string.dismiss_dialog))
                }
            },
            text = {
                OutlinedTextField(
                    value = ui.currentUsername,
                    onValueChange = { onEvent(SettingsEvent.SetTempUsername(it)) },
                    singleLine = true
                )
            }
        )
    }
}


@Composable
fun FeatureDescription(
    @StringRes textRes: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = textRes),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = stringResource(id = textRes), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SuggestLogin(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(R.string.login_header))
        Column(modifier = Modifier.padding(16.dp)) {
            FeatureDescription(
                textRes = R.string.feature_sync,
                icon = Icons.Default.Sync
            )
            FeatureDescription(
                textRes = R.string.feature_groups,
                icon = Icons.Default.Groups
            )
            FeatureDescription(
                textRes = R.string.feature_channels,
                icon = Icons.Default.Campaign
            )
            FeatureDescription(
                textRes = R.string.feature_chat,
                icon = Icons.Default.Chat
            )
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onNavigate(LOGIN) }) {
            Text(
                text = stringResource(id = R.string.log_in_button),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun PropicPlaceholder(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
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
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview(locale = "en")
@Composable
fun AccountNotLoggedDummyScreenPreview() {
    SweckerTheme {
        AccountDummyScreen(
            ui = SettingsUiState(),
            onEvent = {}
        )
    }
}

@Preview(locale = "en")
@Composable
fun AccountLoggedDummyScreenPreview() {
    SweckerTheme {
        AccountDummyScreen(
            ui = SettingsUiState(
                hasUser = true,
                userId = "fakeuser",
                savedUsername = "example",
                savedName = "Example"
            ),
            onEvent = {}
        )
    }
}

@Preview
@Composable
fun PropicPlaceholderPreview() {
    PropicPlaceholder() {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier
                .wrapContentHeight()
                .background(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = CircleShape
                )
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.error
        )
    }
}
