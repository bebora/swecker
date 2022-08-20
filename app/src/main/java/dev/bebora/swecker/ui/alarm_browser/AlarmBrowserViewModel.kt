package dev.bebora.swecker.ui.alarm_browser

import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.alarm_browser.AlarmRepository
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.local.LocalAlarmDataProvider
import dev.bebora.swecker.ui.alarm_notification.scheduleExactAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class AlarmBrowserViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val application: Application? = null
) : ViewModel() {
    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(AlarmBrowserUIState())
    val uiState: StateFlow<AlarmBrowserUIState> = _uiState

    init {
        observeAlarms()
    }

    private fun observeAlarms() {
        viewModelScope.launch {
            repository.getAllAlarms()
                .catch { ex ->
                    _uiState.value = AlarmBrowserUIState(error = ex.message)
                }
                .collect { alarms ->
                    val sortedAlarms = alarms.sortedBy {
                        it.dateTime
                    }
                    if (application != null) {
                        val alarmToSchedule = sortedAlarms.find { al ->
                            (al.dateTime!! > OffsetDateTime.now()) && al.enabled
                        }
                        if (alarmToSchedule != null) {
                            scheduleExactAlarm(
                                context = application.baseContext,
                                dateTime = alarmToSchedule.dateTime!!,
                                name = alarmToSchedule.name
                            )
                        }
                    }
                    val curState = _uiState.value
                    _uiState.value = _uiState
                        .value.copy(
                            alarms = sortedAlarms,
                            filteredAlarms = filterAlarms(
                                alarms = sortedAlarms,
                                selectedDestination = curState.selectedDestination,
                                selectedGroup = curState.selectedGroup,
                                searchKey = curState.searchKey
                            )
                        )
                }
        }
    }

    fun onEvent(event: AlarmBrowserEvent) {
        when (event) {
            is AlarmBrowserEvent.NavBarNavigate -> {
                _uiState.value = _uiState
                    .value.copy(
                        selectedDestination = event.destination,
                        detailsScreenContent = DetailsScreenContent.NONE,
                        selectedAlarm = null,
                        selectedGroup = null,
                        filteredAlarms = filterAlarms(
                            alarms = _uiState.value.alarms,
                            selectedGroup = null,
                            selectedDestination = event.destination,
                            searchKey = ""
                        ),
                        searchKey = ""
                    )
            }

            is AlarmBrowserEvent.AlarmUpdated -> {
                val selectedAlarm = _uiState.value.selectedAlarm
                var detailsScreenContent = _uiState.value.detailsScreenContent

                if (selectedAlarm?.id.equals(event.alarm.id)) {
                    detailsScreenContent = updateDetailsScreenContent()
                }

                _uiState.value = _uiState
                    .value.copy(
                        selectedAlarm = selectedAlarm,
                        detailsScreenContent = detailsScreenContent
                    )

                if (event.success) {
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.insertAlarm(event.alarm)
                    }
                }
            }

            is AlarmBrowserEvent.AlarmPartiallyUpdated -> {
                _uiState.value = _uiState
                    .value.copy(
                        selectedAlarm = event.alarm
                    )
            }

            is AlarmBrowserEvent.AlarmSelected -> {
                _uiState.value = _uiState
                    .value.copy(
                        selectedAlarm = event.alarm,
                        detailsScreenContent = if (event.alarm.alarmType == AlarmType.PERSONAL) {
                            DetailsScreenContent.ALARM_DETAILS
                        } else {
                            DetailsScreenContent.CHAT
                        }
                    )
            }

            //TODO add actual alarm selection logic
            is AlarmBrowserEvent.GroupSelected -> {
                _uiState.value = _uiState
                    .value.copy(
                        selectedGroup = event.group,
                        selectedAlarm = null,
                        filteredAlarms = filterAlarms(
                            _uiState.value.alarms,
                            NavBarDestination.GROUPS,
                            "",
                            event.group
                        ),
                        detailsScreenContent = DetailsScreenContent.GROUP_ALARM_LIST
                    )
            }

            is AlarmBrowserEvent.BackButtonPressed -> {
                if (_uiState.value.dialogContent != DialogContent.NONE) {
                    _uiState.value = _uiState
                        .value.copy(
                            dialogContent = DialogContent.NONE,
                        )
                } else {
                    _uiState.value = _uiState
                        .value.copy(
                            detailsScreenContent = updateDetailsScreenContent()
                        )
                }
            }

            //TODO add group search
            is AlarmBrowserEvent.Search -> {
                val curState = _uiState.value
                _uiState.value = _uiState
                    .value.copy(
                        searchKey = event.key,
                        filteredAlarms = filterAlarms(
                            alarms = curState.alarms,
                            selectedDestination = curState.selectedDestination,
                            searchKey = event.key,
                            selectedGroup = curState.selectedGroup
                        )
                    )
            }

            is AlarmBrowserEvent.ChatTopBarPressed -> {
                _uiState.value = _uiState
                    .value.copy(
                        detailsScreenContent = DetailsScreenContent.ALARM_DETAILS
                    )
            }

            is AlarmBrowserEvent.DialogOpened -> {
                _uiState.value = _uiState
                    .value.copy(
                        dialogContent = event.type
                    )
            }


        }
    }

    private fun filterAlarms(
        alarms: List<Alarm>,
        selectedDestination: NavBarDestination,
        searchKey: String = "",
        selectedGroup: Group?
    ): List<Alarm>? {
        var res: List<Alarm>? = null

        when (selectedDestination) {
            NavBarDestination.PERSONAL -> res =
                alarms.filter { al -> al.alarmType == AlarmType.PERSONAL }
            NavBarDestination.HOME -> res = alarms
            else -> {}
        }

        if (selectedGroup != null) {
            res = alarms.filter { al -> al.groupId == selectedGroup.id }
        }

        if (searchKey.isNotEmpty()) {
            res = res?.filter { al -> al.name.contains(searchKey) }
        }

        return res
    }

    private fun updateDetailsScreenContent(): DetailsScreenContent {
        val curState = _uiState.value

        return when (curState.detailsScreenContent) {
            DetailsScreenContent.ALARM_DETAILS -> {
                if (curState.selectedAlarm?.alarmType != AlarmType.PERSONAL) {
                    DetailsScreenContent.CHAT
                } else {
                    DetailsScreenContent.NONE
                }
            }
            DetailsScreenContent.GROUP_ALARM_LIST -> {
                DetailsScreenContent.NONE
            }

            DetailsScreenContent.CHAT -> {
                if (curState.selectedGroup != null) {
                    DetailsScreenContent.GROUP_ALARM_LIST
                } else {
                    DetailsScreenContent.NONE
                }
            }
            DetailsScreenContent.NONE ->
                DetailsScreenContent.NONE
        }
    }
}


data class AlarmBrowserUIState(
    val alarms: List<Alarm> = emptyList(),
    val filteredAlarms: List<Alarm>? = null,
    val groups: List<Group> = LocalAlarmDataProvider.allGroups,
    val selectedAlarm: Alarm? = null,
    val selectedGroup: Group? = null,
    val detailsScreenContent: DetailsScreenContent = DetailsScreenContent.NONE,
    val dialogContent: DialogContent = DialogContent.NONE,
    val searchKey: String = String(),
    val error: String? = "",
    val selectedDestination: NavBarDestination = NavBarDestination.HOME
)

enum class DetailsScreenContent {
    NONE,
    CHAT,
    GROUP_ALARM_LIST,
    ALARM_DETAILS
}

enum class DialogContent {
    NONE,
    ADD_ALARM,
    ADD_GROUP,
    ADD_CHANNEL,
    ADD_CONTACT,
    CONTACTS
}

enum class NavBarDestination {
    HOME,
    PERSONAL,
    GROUPS,
    CHANNELS
}

fun getNavbarIcon(name: String, isSelected: Boolean): ImageVector {
    return when (name) {
        "Home" -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
        "Personal" -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
        "Groups" -> if (isSelected) Icons.Filled.Groups else Icons.Outlined.Groups
        "Channels" -> if (isSelected) Icons.Filled.Campaign else Icons.Outlined.Campaign
        else -> Icons.Default.Error
    }
}
