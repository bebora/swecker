package dev.bebora.swecker.ui.add_alarm

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.ui.alarm_browser.alarm_details.AlarmDetails

@Composable
fun AddAlarmContent(
    modifier: Modifier = Modifier,
    viewModel: AddAlarmViewModel,
    group: Group?,
    onBackPressed: () -> Unit,
    userId: String?
) {
    val alarm = viewModel.alarm.collectAsState()
    AlarmDetails(
        modifier = modifier,
        alarm = alarm.value,
        isReadOnly = false,
        onAlarmPartiallyUpdated = viewModel::onAlarmPartiallyUpdate,
        onUpdateCompleted = { al, b ->
            viewModel.onUpdateCompleted(al, b, group, userId)
            onBackPressed()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmAppBar(
    modifier: Modifier = Modifier,
    colors: TopAppBarColors,
    onBackPressed: () -> Unit,
) {
    SmallTopAppBar(
        colors = colors,
        modifier = modifier,
        title = { Text(text = "Add alarm", textAlign = TextAlign.Center) },
        navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        actions = {
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit,
    group: Group? = null,
    userId: String? = null,
    addAlarmViewModel: AddAlarmViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            AddAlarmAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                onBackPressed = {
                    addAlarmViewModel.OnUpdateCanceled()
                    onGoBack()
                }
            )
        }
    ) {
        AddAlarmContent(
            modifier = modifier.padding(it),
            viewModel = addAlarmViewModel,
            onBackPressed = onGoBack,
            group = group,
            userId= userId,
            )
    }
}

@Composable
fun AddAlarmDialog(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit,
    group: Group? = null,
    userId: String? = null,
    addAlarmViewModel: AddAlarmViewModel = hiltViewModel()
) {
    Dialog(
        onDismissRequest = onGoBack,
    ) {
        Surface(
            modifier = Modifier.fillMaxHeight(0.9f),
            shape = ShapeDefaults.ExtraLarge,
        ) {
            AddAlarmContent(
                modifier = modifier.padding(16.dp),
                viewModel = addAlarmViewModel,
                onBackPressed = onGoBack,
                group = group,
                userId= userId,
            )
        }


    }
}
