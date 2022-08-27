package dev.bebora.swecker.ui.alarm_browser

import android.app.Application
import android.util.Log
import androidx.compose.animation.core.MutableTransitionState
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
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.service.ChatService
import dev.bebora.swecker.ui.alarm_notification.cancelAlarm
import dev.bebora.swecker.ui.alarm_notification.scheduleExactAlarm
import dev.bebora.swecker.ui.utils.onError
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import java.time.Clock
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AlarmBrowserViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val chatService: ChatService,
    private val authService: AuthService,
    private val accountsService: AccountsService,
    private val alarmProviderService: AlarmProviderService,
    private val iODispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val application: Application? = null
) : ViewModel() {
    // UI state exposed to the UI
    var uiState by mutableStateOf(AlarmBrowserUIState())
        private set

    private val userInfoChanges = authService.getUserInfoChanges()

    private var usersAlreadyRequested: MutableSet<String> = mutableSetOf()

    private var mutableUsersData: MutableMap<String, User> = mutableMapOf()

    private var groupsCollectorJob: Job? = null

    private var alarmsCollectorJob: Job? = null

    private var channelsCollectorJob: Job? = null

    private var searchChannelsJob: Job? = null

    private var messagesCollectorJob: Job? = null

    init {
        viewModelScope.launch {
            userInfoChanges.collect {
                accountsService.getUser(
                    userId = authService.getUserId(),
                    onSuccess = {
                        Log.d("SWECKER-GET-COLLECT", "Preso user da auth, ed è $it")
                        uiState = uiState.copy(
                            me = it
                        )
                    },
                    onError = ::onError
                )
                groupsCollectorJob?.cancel() // Remove the current collector
                groupsCollectorJob = viewModelScope.launch {
                    alarmProviderService.getUserGroups(
                        authService.getUserId()
                    ).collect { groupsList ->
                        uiState = uiState.copy(
                            groups = groupsList.map { thinGroup ->
                                thinGroup.toGroup()
                            }.map { group ->
                                val firstGroupAlarm = uiState.alarms.firstOrNull { alarm ->
                                    alarm.groupId == group.id
                                }
                                group.copy(
                                    firstAlarmDateTime = firstGroupAlarm?.dateTime,
                                    firstAlarmName = firstGroupAlarm?.name ?: ""
                                )
                            }
                        )
                    }
                }
                channelsCollectorJob?.cancel() // Remove the current collector
                channelsCollectorJob = viewModelScope.launch {
                    alarmProviderService.getUserChannels(
                        authService.getUserId()
                    ).collect { channelsList ->
                        uiState = uiState.copy(
                            channels = channelsList.map {
                                it.toGroup()
                            }.map { channel ->
                                val firstChannelAlarm = uiState.alarms.firstOrNull { alarm ->
                                    alarm.groupId == channel.id
                                }
                                channel.copy(
                                    firstAlarmDateTime = firstChannelAlarm?.dateTime,
                                    firstAlarmName = firstChannelAlarm?.name ?: ""
                                )
                            }
                        )
                    }
                }

                alarmsCollectorJob?.cancel()
                alarmsCollectorJob = viewModelScope.launch {
                    repository.getAllAlarms()
                        .catch { ex ->
                            uiState = AlarmBrowserUIState(error = ex.message)
                        }
                        .collect { alarms ->
                            if (application != null) {
                                val alarmToSchedule = alarms.find { al ->
                                    (al.dateTime!! > OffsetDateTime.now()) && al.enabled
                                }
                                if (alarmToSchedule != null) {
                                    scheduleExactAlarm(
                                        context = application.baseContext,
                                        dateTime = alarmToSchedule.dateTime!!,
                                        name = alarmToSchedule.name
                                    )
                                } else {
                                    cancelAlarm(context = application.baseContext)
                                }
                            }
                            val curState = uiState
                            uiState = uiState.copy(
                                alarms = alarms,
                                filteredAlarms = filterAlarms(
                                    alarms = alarms,
                                    selectedDestination = curState.selectedDestination,
                                    selectedGroup = curState.selectedGroup
                                        ?: curState.selectedChannel,
                                    searchKey = curState.searchKey
                                )
                            )
                        }
                }
            }
        }
    }


    private fun fetchUsersData(usersIds: List<String>) {
        usersIds
            .filter { !uiState.usersData.containsKey(it) }
            .forEach { userId ->
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

    fun onEvent(event: AlarmBrowserEvent) {
        when (event) {
            is AlarmBrowserEvent.NavBarNavigate -> {
                uiState = uiState.copy(
                    selectedDestination = event.destination,
                    detailsScreenContent = DetailsScreenContent.NONE,
                    animatedDetailsScreenContent = DetailsScreenContent.NONE,
                    selectedAlarm = null,
                    selectedGroup = null,
                    selectedChannel = null,
                    extraChannels = emptyList(),
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
                var mutableTransitionState = MutableTransitionState(true).apply {
                    targetState = true
                }

                if (selectedAlarm?.id.equals(event.alarm.id) &&
                    detailsScreenContent == DetailsScreenContent.ALARM_DETAILS) {
                    detailsScreenContent = detailsScreenContentOnGoBack()
                    if (uiState.detailsScreenContent != DetailsScreenContent.NONE) {
                        mutableTransitionState = MutableTransitionState(true).apply {
                            targetState = false
                        }
                    }
                }

                uiState = uiState.copy(
                    selectedAlarm = selectedAlarm,
                    detailsScreenContent = detailsScreenContent,
                    mutableTransitionState = mutableTransitionState
                )

                if (event.success) {
                    CoroutineScope(iODispatcher).launch {
                        repository.updateAlarm(
                            event.alarm.copy(
                                timeStamp = OffsetDateTime.now()
                                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            ),
                            userId = if(shouldSendUserId()) {uiState.me.id} else {null}
                        )
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
                    detailsScreenContent = if (event.alarm.alarmType == AlarmType.PERSONAL || !event.alarm.enableChat) {
                        DetailsScreenContent.ALARM_DETAILS
                    } else {
                        DetailsScreenContent.CHAT
                    },
                    animatedDetailsScreenContent = if (event.alarm.alarmType == AlarmType.PERSONAL || !event.alarm.enableChat) {
                        DetailsScreenContent.ALARM_DETAILS
                    } else {
                        DetailsScreenContent.CHAT
                    },
                    mutableTransitionState = MutableTransitionState(false).apply {
                        targetState = true
                    }
                )
                messagesCollectorJob?.cancel()
                messagesCollectorJob = viewModelScope.launch {
                    chatService.getMessages(event.alarm.id)
                        .collect {
                            Log.d("SWECKER-CHAT-DEBUG", "Got from firebase: $it")
                            uiState = uiState.copy(
                                messages = it.reversed()
                            )
                            fetchUsersData(usersIds = it.reversed().map { msg -> msg.uId })
                        }
                }
            }

            is AlarmBrowserEvent.AlarmDeleted -> {
                viewModelScope.launch {
                    repository.deleteAlarm(
                        alarm = event.alarm,
                        userId = if(shouldSendUserId()) {uiState.me.id} else {null}
                    )
                }
                var detailsScreenContent: DetailsScreenContent = DetailsScreenContent.NONE

                if(uiState.selectedGroup != null){
                    detailsScreenContent = DetailsScreenContent.GROUP_ALARM_LIST
                }else if(uiState.selectedChannel != null){
                    detailsScreenContent = DetailsScreenContent.CHANNEL_ALARM_LIST
                }
                uiState = uiState.copy(
                    detailsScreenContent = detailsScreenContent,
                    animatedDetailsScreenContent = detailsScreenContent,
                    dialogContent = DialogContent.NONE,
                    selectedAlarm = null,
                )
            }

            is AlarmBrowserEvent.GroupSelected -> {
                uiState = uiState.copy(
                    selectedGroup = event.group,
                    selectedAlarm = null,
                    selectedChannel = null,
                    filteredAlarms = filterAlarms(
                        uiState.alarms,
                        NavBarDestination.GROUPS,
                        "",
                        event.group
                    ),
                    detailsScreenContent = DetailsScreenContent.GROUP_ALARM_LIST,
                    animatedDetailsScreenContent = DetailsScreenContent.GROUP_ALARM_LIST,
                    mutableTransitionState = MutableTransitionState(false).apply {
                        targetState = true
                    }
                )
                fetchUsersData(usersIds = event.group.members)
            }

            is AlarmBrowserEvent.ChannelSelected -> {
                uiState = uiState.copy(
                    selectedChannel = event.channel,
                    selectedAlarm = null,
                    selectedGroup = null,
                    filteredAlarms = filterAlarms(
                        uiState.alarms,
                        NavBarDestination.GROUPS,
                        "",
                        event.channel
                    ),
                    detailsScreenContent = DetailsScreenContent.CHANNEL_ALARM_LIST,
                    animatedDetailsScreenContent = DetailsScreenContent.CHANNEL_ALARM_LIST,
                    mutableTransitionState = MutableTransitionState(false).apply {
                        targetState = true
                    }
                )
                fetchUsersData(usersIds = event.channel.members)
            }

            is AlarmBrowserEvent.BackButtonPressed -> {
                if (uiState.dialogContent != DialogContent.NONE) {
                    uiState = uiState.copy(
                        dialogContent = DialogContent.NONE,
                    )
                } else {
                    uiState = uiState.copy(
                        detailsScreenContent = detailsScreenContentOnGoBack(),
                        mutableTransitionState = MutableTransitionState(true).apply {
                            targetState = false
                        }
                    )
                }

            }

            //TODO add group search
            is AlarmBrowserEvent.SearchAlarms -> {
                val curState = uiState
                uiState = uiState.copy(
                    searchKey = event.key,
                    filteredAlarms = filterAlarms(
                        alarms = curState.alarms,
                        selectedDestination = curState.selectedDestination,
                        searchKey = event.key,
                        selectedGroup = curState.selectedGroup ?: curState.selectedChannel
                    )
                )
            }

            is AlarmBrowserEvent.SearchGroups -> {
                uiState = uiState.copy(
                    searchKey = event.key,
                )
                searchDebounced(event.key)
            }

            is AlarmBrowserEvent.DetailsOpened -> {
                uiState = uiState.copy(
                    detailsScreenContent = event.type,
                    mutableTransitionState = MutableTransitionState(true).apply {
                        targetState = false
                    }
                )
            }

            is AlarmBrowserEvent.DialogOpened -> {
                uiState = uiState.copy(
                    dialogContent = event.type
                )
            }


            is AlarmBrowserEvent.SendMessage -> {
                chatService.sendMessage(
                    chatId = uiState.selectedAlarm?.id ?: "",
                    senderId = uiState.me.id,
                    text = event.text,
                    onResult = {
                        if (it != null) {
                            onError(it)
                        }
                    }
                )
            }

            is AlarmBrowserEvent.OnTransitionCompleted -> {
                val mutableTransitionState = uiState.mutableTransitionState
                if (!mutableTransitionState.targetState) {
                    uiState = uiState.copy(
                        mutableTransitionState = MutableTransitionState(false).apply {
                            targetState = true
                        },
                        animatedDetailsScreenContent = uiState.detailsScreenContent,
                    )
                }
            }

            is AlarmBrowserEvent.JoinChannel -> {
                alarmProviderService.joinChannel(
                    userId = uiState.me.id,
                    channelId = event.channel.id,
                    onComplete = {
                        if (it != null) {
                            onError(it)
                        }
                    }
                )
                uiState = uiState.copy(
                    extraChannels = uiState.extraChannels.minus(event.channel)
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

        if (searchKey.isNotBlank()) {
            res = res?.filter { al -> al.name.contains(searchKey, ignoreCase = true) }
        }

        return res
    }

    private fun detailsScreenContentOnGoBack(): DetailsScreenContent {
        val curState = uiState

        return when (curState.detailsScreenContent) {
            DetailsScreenContent.ALARM_DETAILS -> {
                if (curState.selectedAlarm?.alarmType != AlarmType.PERSONAL &&
                    curState.selectedAlarm?.enableChat == true
                ) {
                    DetailsScreenContent.CHAT
                } else {
                    DetailsScreenContent.NONE
                }
            }
            DetailsScreenContent.GROUP_ALARM_LIST, DetailsScreenContent.CHANNEL_ALARM_LIST -> {
                DetailsScreenContent.NONE
            }

            DetailsScreenContent.CHAT -> {
                if (curState.selectedGroup != null) {
                    DetailsScreenContent.GROUP_ALARM_LIST
                } else if (curState.selectedChannel != null) {
                    DetailsScreenContent.CHANNEL_ALARM_LIST
                } else {
                    DetailsScreenContent.NONE
                }
            }

            DetailsScreenContent.GROUP_DETAILS -> {
                DetailsScreenContent.GROUP_ALARM_LIST
            }

            DetailsScreenContent.CHANNEL_DETAILS -> {
                DetailsScreenContent.CHANNEL_ALARM_LIST
            }

            DetailsScreenContent.NONE ->
                DetailsScreenContent.NONE
        }
    }

    private fun searchDebounced(searchText: String) {
        if (uiState.me.id.isBlank()) {
            Log.d("SWECKER-", "id vuoto")
            return
        }
        /*uiState = uiState.copy(
            processingQuery = true
        )*/
        searchChannelsJob?.cancel()
        searchChannelsJob = viewModelScope.launch(iODispatcher) {
            delay(250)
            alarmProviderService.searchNewChannels(
                from = uiState.me,
                query = searchText,
                onError = { Log.e("SWECKER-SEARCH-CH-ERR", it.message ?: "Generic search error") },
                onSuccess = { channelsList ->
                    uiState = uiState.copy(
                        extraChannels = channelsList.map { it.toGroup() },
                        // processingQuery = false
                    )
                }
            )
        }
    }

    private fun shouldSendUserId():Boolean{
        if(uiState.selectedGroup != null){
            return uiState.selectedGroup!!.owner != uiState.me.id
        }
        if(uiState.selectedChannel != null){
            return uiState.selectedChannel!!.owner != uiState.me.id
        }
        if(uiState.selectedAlarm != null){
            return uiState.selectedAlarm!!.alarmType == AlarmType.PERSONAL
        }
        return true
    }
}


data class AlarmBrowserUIState(
    val alarms: List<Alarm> = emptyList(),
    val filteredAlarms: List<Alarm>? = null,
    val groups: List<Group> = LocalAlarmDataProvider.allGroups,
    val channels: List<Group> = LocalAlarmDataProvider.allChannels,
    val selectedAlarm: Alarm? = null,
    val selectedGroup: Group? = null,
    val selectedChannel: Group? = null,
    val detailsScreenContent: DetailsScreenContent = DetailsScreenContent.NONE,
    val dialogContent: DialogContent = DialogContent.NONE,
    val searchKey: String = String(),
    val extraChannels: List<Group> = emptyList(),
    val error: String? = "",
    val selectedDestination: NavBarDestination = NavBarDestination.HOME,
    val me: User = User(),
    val messages: List<Message> = emptyList(),
    val usersData: Map<String, User> = emptyMap(),
    val mutableTransitionState: MutableTransitionState<Boolean> = MutableTransitionState(false),
    val animatedDetailsScreenContent: DetailsScreenContent = DetailsScreenContent.NONE
)

enum class DetailsScreenContent {
    NONE,
    CHAT,
    GROUP_ALARM_LIST,
    GROUP_DETAILS,
    CHANNEL_DETAILS,
    CHANNEL_ALARM_LIST,
    ALARM_DETAILS
}

enum class DialogContent {
    NONE,
    ADD_ALARM,
    ADD_GROUP,
    ADD_CHANNEL,
    ADD_CONTACT,
    CONTACT_BROWSER,
}

enum class NavBarDestination {
    HOME,
    PERSONAL,
    GROUPS,
    CHANNELS
}
