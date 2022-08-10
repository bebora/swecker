package dev.bebora.swecker.ui.alarm_browser

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AlarmBrowserViewModel @Inject constructor(
    private val repository: AlarmRepository
) : ViewModel() {
    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(AlarmBrowserUIState())
    val uiState: StateFlow<AlarmBrowserUIState> = _uiState


    fun onEvent(event: AlarmBrowserEvent) {
        when (event) {
            is AlarmBrowserEvent.NavBarNavigate -> {
                _uiState.value = _uiState
                    .value.copy(
                        selectedDestination = event.destination
                    )
            }
            else -> {}

        }
    }
}


data class AlarmBrowserUIState(
    val alarms: List<Alarm> = emptyList(),
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
