package dev.bebora.swecker.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.common.composable.PasswordField
import dev.bebora.swecker.data.service.impl.AccountServiceImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onGoToSignup: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onGoBack: () -> Unit = {}
) {
    val uiState = viewModel.uiState

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(R.string.login_appbar_title)) },
                navigationIcon = {
                    IconButton(onClick = { onGoBack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
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
                Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.onSurface)
                OutlinedTextField(
                    singleLine = true,
                    value = uiState.email,
                    onValueChange = { newValue ->
                        viewModel.onEvent(LoginEvent.SetTempEmail(newValue))
                    },
                    placeholder = { Text(text = stringResource(R.string.email_placeholder)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = stringResource(R.string.email_placeholder)
                        )
                    },
                    modifier = Modifier
                        .padding(
                            horizontal = 32.dp,
                            vertical = 8.dp
                        )
                        .fillMaxWidth()
                )

                PasswordField(
                    value = uiState.password,
                    onNewValue = { newValue ->
                        viewModel.onEvent(LoginEvent.SetTempPassword(newValue))
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
                    onClick = { viewModel.onEvent(LoginEvent.SignInClick(onLoginSuccess)) },
                ) {
                    Text(
                        text = stringResource(id = R.string.log_in_button),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                TextButton(
                    onClick = { onGoToSignup() },
                ) {
                    Text(
                        text = stringResource(R.string.ask_signup),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        viewModel = LoginViewModel(
            accountService = AccountServiceImpl()
        )
    )
}
