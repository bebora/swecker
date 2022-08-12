package dev.bebora.swecker.ui.settings.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.theme.SweckerTheme

@Composable
fun PaletteBox(
    colorScheme: ColorScheme,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onEvent: () -> Unit
) {
    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onEvent() }
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(16.dp)
            )) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.5f)
                    .background(colorScheme.primary)
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .fillMaxHeight()
                        .background(colorScheme.secondary)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.tertiary)
                )
            }
        }
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.Done,
                contentDescription = "Color scheme selected",
                tint = colorScheme.onSurface,
                modifier = Modifier
                    .background(
                        color = colorScheme.surface,
                        shape = RoundedCornerShape(percent = 100)
                    )
                    .padding(4.dp)
            )
        }
    }
}


@Preview(showBackground = false, widthDp = 100, heightDp = 100)
@Composable
fun PaletteBoxPreview() {
    SweckerTheme {
        PaletteBox(
            colorScheme = MaterialTheme.colorScheme,
            selected = true
        ) {}
    }
}