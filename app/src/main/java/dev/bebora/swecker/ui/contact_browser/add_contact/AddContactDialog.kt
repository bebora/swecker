package dev.bebora.swecker.ui.contact_browser.add_contact

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddContactDialog(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: AddContactViewModel = hiltViewModel(),
) {
    val ui = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

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
    Dialog(onDismissRequest = onGoBack) {
        Surface(
            modifier = Modifier
                .fillMaxHeight(0.9f),
            shape = ShapeDefaults.ExtraLarge
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    SmallTopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.height(60.dp)
                            ) {
                                Text(text = stringResource(R.string.add_contact_title))
                                Spacer(modifier = Modifier.width(16.dp))
                                if (ui.uploadingFriendshipRequest || ui.processingQuery) {
                                    CircularProgressIndicator()
                                }
                            }
                        },
                        navigationIcon = {

                        },
                        scrollBehavior = scrollBehavior,
                        actions = {
                            IconButton(onClick = { onGoBack() }) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = stringResource(id = R.string.go_back)
                                )
                            }
                        }
                    )
                },
            ) { padding ->
                AddContactContent(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp),
                    keyboardController = keyboardController,
                    ui = ui,
                    onNavigate = onNavigate,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}

