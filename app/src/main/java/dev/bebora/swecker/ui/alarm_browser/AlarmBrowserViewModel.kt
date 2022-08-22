package dev.bebora.swecker.ui.alarm_browser

import android.app.Application
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.*
import dev.bebora.swecker.data.alarm_browser.AlarmRepository
import dev.bebora.swecker.data.local.LocalAlarmDataProvider
import dev.bebora.swecker.data.service.AccountsService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.service.ChatService
import dev.bebora.swecker.ui.alarm_notification.scheduleExactAlarm
import dev.bebora.swecker.ui.utils.onError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class AlarmBrowserViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val chatService: ChatService,
    private val authService: AuthService,
    private val accountsService: AccountsService,
    private val application: Application? = null
) : ViewModel() {
    // UI state exposed to the UI
    var uiState by mutableStateOf(AlarmBrowserUIState())
        private set

    private val userInfoChanges = authService.getUserInfoChanges()

    private var usersAlreadyRequested: MutableSet<String> = mutableSetOf()

    private var mutableUsersData: MutableMap<String, User> = mutableMapOf()

    init {
        observeAlarms()
        viewModelScope.launch {
            accountsService.getUser(authService.getUserId(), ::onError) {
                uiState = uiState.copy(
                    me = it,
                )
                Log.d("SWECKER-GET", "Preso user da storage, ed è $it")
            }
            userInfoChanges.collect {
                /*uiState = uiState.copy(
                    hasUser = authService.hasUser(),
                    userId = authService.getUserId(),
                )*/
                accountsService.getUser(
                    userId = authService.getUserId(),
                    onSuccess = {
                        uiState = uiState.copy(
                            me = it
                        )
                    },
                    onError = ::onError
                )
            }
        }
        viewModelScope.launch {
            chatService.getMessages("testchat")
                .collect {
                    uiState = uiState.copy(
                        messages = it
                    )
                    fetchUsersData(messages = it)
                }
        }
    }

    private fun fetchUsersData(messages: List<Message>) {
        messages
            .map { it.uId }
            .filter { !uiState.usersData.containsKey(it) }
            .forEach {userId ->
                if (!usersAlreadyRequested.contains(userId)) {
                    usersAlreadyRequested.add(userId)
                    accountsService.getUser(
                        userId = userId,
                        onError = { onError(it) },
                        onSuccess = {
                            mutableUsersData[userId] = it
                            uiState = uiState.copy(
                                usersData = mutableUsersData.toMap()
                            )
                        }
                    )
                }
            }
    }

    private fun observeAlarms() {
        viewModelScope.launch {
            repository.getAllAlarms()
                .catch { ex ->
                    uiState = AlarmBrowserUIState(error = ex.message)
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
                    val curState = uiState
                    uiState = uiState.copy(
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
                uiState = uiState.copy(
                    selectedDestination = event.destination,
                    detailsScreenContent = DetailsScreenContent.NONE,
                    selectedAlarm = null,
                    selectedGroup = null,
                    filteredAlarms = filterAlarms(
                        alarms = uiState.alarms,
                        selectedGroup = null,
                        selectedDestination = event.destination,
                        searchKey = ""
                    ),
                    searchKey = ""
                )
            }

            is AlarmBrowserEvent.AlarmUpdated -> {
                val selectedAlarm = uiState.selectedAlarm
                var detailsScreenContent = uiState.detailsScreenContent

                if (selectedAlarm?.id.equals(event.alarm.id)) {
                    detailsScreenContent = updateDetailsScreenContent()
                }

                uiState = uiState.copy(
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
                uiState = uiState.copy(
                    selectedAlarm = event.alarm
                )
            }

            is AlarmBrowserEvent.AlarmSelected -> {
                uiState = uiState.copy(
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
                uiState = uiState.copy(
                    selectedGroup = event.group,
                    selectedAlarm = null,
                    filteredAlarms = filterAlarms(
                        uiState.alarms,
                        NavBarDestination.GROUPS,
                        "",
                        event.group
                    ),
                    detailsScreenContent = DetailsScreenContent.GROUP_ALARM_LIST
                )
            }

            is AlarmBrowserEvent.BackButtonPressed -> {
                if (uiState.dialogContent != DialogContent.NONE) {
                    uiState = uiState.copy(
                        dialogContent = DialogContent.NONE,
                    )
                } else {
                    uiState = uiState.copy(
                        detailsScreenContent = updateDetailsScreenContent()
                    )
                }
            }

            //TODO add group search
            is AlarmBrowserEvent.Search -> {
                val curState = uiState
                uiState = uiState.copy(
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
                uiState = uiState.copy(
                    detailsScreenContent = DetailsScreenContent.ALARM_DETAILS
                )
            }

            is AlarmBrowserEvent.DialogOpened -> {
                uiState = uiState.copy(
                    dialogContent = event.type
                )
            }

            is AlarmBrowserEvent.OpenChatTEMP -> {
                uiState = uiState.copy(
                    detailsScreenContent = DetailsScreenContent.CHAT
                )
            }

            is AlarmBrowserEvent.SendMessageTEMP -> {
                chatService.sendMessage(
                    chatId = "testchat",
                    senderId = uiState.me.id,
                    text = event.text,
                    onResult = {
                        if (it != null) {
                            onError(it)
                        }
                    }
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
        val curState = uiState

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
    val selectedDestination: NavBarDestination = NavBarDestination.HOME,
    val me: User = User(),
    val messages: List<Message> = emptyList(),
    val usersData: Map<String, User> = emptyMap()
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
    CONTACT_BROWSER
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
