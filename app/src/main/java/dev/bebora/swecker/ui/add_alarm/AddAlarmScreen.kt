package dev.bebora.swecker.ui.add_alarm

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.alarm_browser.FakeAlarmRepository
import dev.bebora.swecker.ui.alarm_browser.alarm_details.AlarmDetails
import dev.bebora.swecker.ui.alarm_browser.alarm_details.nextEnabledDate
import dev.bebora.swecker.ui.theme.SweckerTheme
import dev.bebora.swecker.util.TestConstants
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun AddAlarmContent(
    modifier: Modifier = Modifier,
    viewModel: AddAlarmViewModel,
    group: Group?,
    onBackPressed: () -> Unit,
    userId: String?,
    alarmType: AlarmType,
    showButtons: Boolean
) {
    val alarm = viewModel.vmAlarm
    AlarmDetails(
        modifier = modifier,
        alarm = alarm,
        showEnableChat = group != null,
        canDelete = false,
        onAlarmPartiallyUpdated = viewModel::onAlarmPartiallyUpdate,
        onUpdateCompleted = { al, b ->
            viewModel.onUpdateCompleted(al, b, group, userId, alarmType)
            onBackPressed()
        },
        showButtons = showButtons
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
        title = {
            Text(
                text = stringResource(R.string.add_alarm_title),
                textAlign = TextAlign.Center
            )
        },
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
    alarmType: AlarmType,
    addAlarmViewModel: AddAlarmViewModel = hiltViewModel()
) {
    val alarm = addAlarmViewModel.vmAlarm
    Scaffold(
        topBar = {
            AddAlarmAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                onBackPressed = {
                    addAlarmViewModel.onUpdateCanceled()
                    onGoBack()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .imePadding()
                    .testTag(TestConstants.confirm),
                onClick = {
                    val enabledRepetition = alarm.enabledDays.reduceRight { a, b -> a || b }
                    val date = if (!enabledRepetition) {
                        alarm.localDate
                    } else {
                        nextEnabledDate(
                            enabledDays = alarm.enabledDays,
                            time = alarm.localTime!!
                        ).toLocalDate()
                    }
                    addAlarmViewModel.onUpdateCompleted(
                        alarm.copy(
                            localDate = date,
                            dateTime = ZonedDateTime.of(
                                date,
                                alarm.localTime,
                                ZoneId.systemDefault()
                            ).toOffsetDateTime()
                        ),
                        alarmType = alarmType,
                        group = group,
                        userId = userId,
                        success = true
                    )
                    onGoBack()
                }) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Finish alarm creation"
                )
            }
        }
    ) {
        AddAlarmContent(
            modifier = modifier.padding(it),
            viewModel = addAlarmViewModel,
            onBackPressed = onGoBack,
            group = group,
            userId = userId,
            alarmType = alarmType,
            showButtons = false
        )
    }
}

@Composable
fun AddAlarmDialog(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit,
    group: Group? = null,
    userId: String? = null,
    alarmType: AlarmType,
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
                userId = userId,
                alarmType = alarmType,
                showButtons = true
            )
        }
    }
}

@Preview(showBackground = true, locale = "en")
@Composable
fun AddAlarmContentPreview() {
    SweckerTheme {
        AddAlarmScreen(
            addAlarmViewModel = AddAlarmViewModel(
                repository = FakeAlarmRepository()
            ),
            group = null,
            onGoBack = { /*TODO*/ },
            userId = "luca",
            alarmType = AlarmType.PERSONAL
        )
    }
}
