package dev.bebora.swecker.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.SETTINGS
import dev.bebora.swecker.SIGNUP
import dev.bebora.swecker.data.service.impl.AccountServiceImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit = {}
) {
    val uiState = viewModel.uiState

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.onSurface)
        OutlinedTextField(
            singleLine = true,
            value = uiState.email,
            onValueChange = { viewModel.onEvent(LoginEvent.SetTempEmail(it)) },
            placeholder = { Text(text = "Email") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email"
                )
            }
        )

        PasswordField(
            value = uiState.password,
            placeholder = "Password",
            onValueChange = {
                viewModel.onEvent(LoginEvent.SetTempPassword(it))
            }
        )
        Button(
            onClick = { viewModel.onEvent(LoginEvent.SignInClick) },
        ) {
            Text(text = "Log in", style = MaterialTheme.typography.labelSmall)
        }
        Button(
            onClick = { onNavigate(SETTINGS) },
        ) {
            Text(text = "Go to settings", style = MaterialTheme.typography.labelSmall)
        }
        Button(
            onClick = { onNavigate(SIGNUP) },
        ) {
            Text(text = "Signup", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    val icon = if (isVisible) Icons.Default.Visibility
    else Icons.Default.VisibilityOff

    val visualTransformation = if (isVisible) VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(text = placeholder) },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(imageVector = icon, contentDescription = "Visibility")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation
    )
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        viewModel = LoginViewModel(
            accountService = AccountServiceImpl()
        ),
        onNavigate = {}
    )
}
