package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmRepository
import dev.bebora.swecker.data.AlarmRepositoryImpl
import dev.bebora.swecker.data.local.LocalAlarmDataProvider
import dev.bebora.swecker.ui.theme.SweckerTheme

class AlarmBrowserScreen {

    @Composable
    fun AlarmList(
        alarms: List<Alarm>,
        modifier: Modifier = Modifier,
        onEvent: () -> Unit,
        ){
        LazyColumn(contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()){
            items(alarms) {al ->
                AlarmCard(alarm = al, modifier = modifier, onEvent = onEvent)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AlarmListTest(){
        SweckerTheme() {
            AlarmList(alarms = LocalAlarmDataProvider.allAlarms, onEvent = { })
        }
    }
}