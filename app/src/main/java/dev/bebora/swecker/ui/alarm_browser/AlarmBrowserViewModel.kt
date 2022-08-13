package dev.bebora.swecker.ui.alarm_browser

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
import dev.bebora.swecker.data.AlarmRepository
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.local.LocalAlarmDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmBrowserViewModel @Inject constructor(
    private val repository: AlarmRepository
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
                    _uiState.value = _uiState
                        .value.copy(
                            alarms = alarms
                        )
                }
        }
    }

    fun onEvent(event: AlarmBrowserEvent) {
        when (event) {
            is AlarmBrowserEvent.NavBarNavigate -> {
                var filteredAlarm: List<Alarm>? = null
                if (event.destination == NavBarDestination.PERSONAL) {
                    filteredAlarm =
                        _uiState.value.alarms.filter { al -> al.alarmType == AlarmType.PERSONAL }
                }
                _uiState.value = _uiState
                    .value.copy(
                        selectedDestination = event.destination,
                        isDetailsOpen = false,
                        isGroupOpen = false,
                        selectedAlarm = null,
                        selectedGroup = null,
                        filteredAlarms = filteredAlarm
                    )
            }

            is AlarmBrowserEvent.AlarmUpdated -> {
                _uiState.value = _uiState
                    .value.copy(
                        selectedAlarm = null,
                        isDetailsOpen = false
                    )
                if (event.success) {
                    viewModelScope.launch {
                        repository.updateAlarm(event.alarm)
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
                        isDetailsOpen = true
                    )
            }

            //TODO add actual alarm selection logic
            is AlarmBrowserEvent.GroupSelected -> {
                _uiState.value = _uiState
                    .value.copy(
                        selectedGroup = event.group,
                        selectedAlarm = null,
                        filteredAlarms = event.group.alarms,
                        isDetailsOpen = false,
                        isGroupOpen = true
                    )
            }

            is AlarmBrowserEvent.BackButtonPressed -> {
                if (_uiState.value.isDetailsOpen) {
                    _uiState.value = _uiState
                        .value.copy(
                            isDetailsOpen = false,
                        )
                } else if (_uiState.value.isGroupOpen) {
                    _uiState.value = _uiState
                        .value.copy(
                            isGroupOpen = false,
                            filteredAlarms = emptyList()
                        )
                }
            }

            else -> {}

        }
    }
}


data class AlarmBrowserUIState(
    val alarms: List<Alarm> = emptyList(),
    val filteredAlarms: List<Alarm>? = null,
    val groups: List<Group> = LocalAlarmDataProvider.allGroups,
    val selectedAlarm: Alarm? = null,
    val selectedGroup: Group? = null,
    val isDetailsOpen: Boolean = false,
    val isGroupOpen: Boolean = false,
    val error: String? = "",
    val selectedDestination: NavBarDestination = NavBarDestination.HOME,
)


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
