package dev.bebora.swecker.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.bebora.swecker.R

@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    value: String,
    onNewValue: (String) -> Unit,
) {
    PasswordField(
        modifier = modifier,
        value = value,
        placeholder = R.string.password_placeholder,
        onNewValue = onNewValue
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(
    modifier: Modifier = Modifier,
    value: String,
    @StringRes placeholder: Int,
    onNewValue: (String) -> Unit,
) {
    var isVisible by remember { mutableStateOf(false) }

    val icon = if (isVisible) Icons.Default.Visibility
    else Icons.Default.VisibilityOff

    val visualTransformation = if (isVisible) VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { onNewValue(it) },
        placeholder = { Text(text = stringResource(placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = stringResource(R.string.password_lock)
            )
        },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(imageVector = icon, contentDescription = stringResource(R.string.visibility))
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation
    )
}
