package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmBrowserSearchBar(
    modifier: Modifier = Modifier,
    searchKey: String,
    placeHolderString: String = "",
    onEvent: (AlarmBrowserEvent) -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(1f),
        value = searchKey,
        onValueChange = { searchValue -> onEvent(AlarmBrowserEvent.Search(searchValue)) },
        placeholder = { Text(placeHolderString) },
        leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
        shape = ShapeDefaults.ExtraLarge,
        singleLine = true,
        maxLines = 1
    )
}
