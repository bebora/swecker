package dev.bebora.swecker.ui.settings.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.R
import dev.bebora.swecker.ui.settings.SettingsItem
import dev.bebora.swecker.ui.settings.SettingsSection
import dev.bebora.swecker.ui.theme.SweckerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDummyScreen() {
    //TODO add real data
    val sections = listOf(
        SettingsSection(
            "Luke",
            stringResource(R.string.account_change_name),
            Icons.Outlined.Person
        ),
        SettingsSection(
            "@lucr",
            stringResource(R.string.account_change_username),
            Icons.Outlined.AlternateEmail
        )
    )
    Scaffold(topBar = {
        SmallTopAppBar(
            title = { Text(text = stringResource(R.string.account_title)) },
            navigationIcon = {
                IconButton(onClick = { /*TODO go back*/ }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }
        )
    }) {
        Column(
            Modifier.padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .requiredSize(160.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.error
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(80.dp)
                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
            sections.forEach { section ->
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
                SettingsItem(
                    title = section.title,
                    description = section.description?: "Default description",
                    icon = section.icon,
                    onClick = section.onClick
                )
            }
        }
    }
}

@Preview(locale = "en")
@Composable
fun AccountDummyScreenPreview() {
    SweckerTheme {
        AccountDummyScreen()
    }
}
