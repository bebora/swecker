package dev.bebora.swecker.ui.sign_up

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.common.composable.PasswordField
import dev.bebora.swecker.data.service.impl.AccountsServiceImpl
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.util.UiEvent

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    onGoToLogin: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {},
    onGoBack: () -> Unit = {}
) {
    val uiState = viewModel.uiState

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
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
                title = { Text(text = stringResource(R.string.signup_appbar_title)) },
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
                    if (uiState.loading) {
                        CircularProgressIndicator()
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    keyboardController?.hide()
                },
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = modifier
                    .width(600.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    singleLine = true,
                    value = uiState.email,
                    onValueChange = { newValue ->
                        viewModel.onEvent(SignUpEvent.SetTempEmail(newValue))
                    },
                    placeholder = { Text(text = stringResource(id = R.string.email_placeholder)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = stringResource(id = R.string.email_placeholder)
                        )
                    },
                    modifier = Modifier
                        .padding(
                            horizontal = 32.dp,
                            vertical = 8.dp
                        )
                        .fillMaxWidth(),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    }),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email),
                )

                PasswordField(
                    value = uiState.password,
                    onNewValue = { newValue ->
                        viewModel.onEvent(SignUpEvent.SetTempPassword(newValue))
                    },
                    modifier = Modifier
                        .padding(
                            horizontal = 32.dp,
                            vertical = 8.dp
                        )
                        .fillMaxWidth()
                )
                Button(
                    modifier = Modifier
                        .padding(
                            horizontal = 32.dp,
                            vertical = 8.dp
                        )
                        .fillMaxWidth(),
                    onClick = {
                        keyboardController?.hide()
                        viewModel.onEvent(SignUpEvent.SignInClick(onSignUpSuccess))
                    },
                    enabled = !uiState.loading
                ) {
                    Text(
                        text = stringResource(id = R.string.sign_up_button),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                TextButton(
                    onClick = { onGoToLogin() },
                ) {
                    Text(
                        text = stringResource(R.string.ask_existing_account),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SignupScreenPreview() {
    SignUpScreen(
        viewModel = SignUpViewModel(
            authService = FakeAuthService(),
            accountsService = AccountsServiceImpl()
        )
    )
}
