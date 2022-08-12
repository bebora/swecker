package dev.bebora.swecker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.theme.SweckerTheme


@Composable
fun SettingsSwitch(
    title: String,
    icon: ImageVector,
    checked: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .height(88.dp)
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = icon.name)
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = { _ -> onToggle() })
    }
}

@Preview
@Composable
fun SettingsSwitchPreview() {
    SweckerTheme {
        SettingsSwitch(
            title = "Vibration",
            icon = Icons.Outlined.Vibration,
            checked = true
        ) {

        }
    }
}
