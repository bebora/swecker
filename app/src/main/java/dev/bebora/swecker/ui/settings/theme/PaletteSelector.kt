package dev.bebora.swecker.ui.settings.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.ui.theme.SweckerTheme

@Composable
fun PaletteSelector(palette: ColorScheme, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.5f)
                .background(palette.primary)
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .fillMaxHeight()
                    .background(palette.secondary)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.tertiary)
            )
        }
    }
}


@Preview(showBackground = false, widthDp = 100, heightDp = 100)
@Composable
fun PaletteSelectorPreview() {
    SweckerTheme {
        PaletteSelector(palette = MaterialTheme.colorScheme)
    }
}
