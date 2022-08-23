package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmBrowserSearchBar(
    modifier: Modifier = Modifier,
    searchKey: String,
    placeHolderString: String = "",
    onValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(1f),
        value = searchKey,
        onValueChange = { searchValue -> onValueChange(searchValue) },
        placeholder = { Text(placeHolderString) },
        leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
        shape = ShapeDefaults.ExtraLarge,
        singleLine = true,
        maxLines = 1,
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
        ),
    )
}
