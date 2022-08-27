package dev.bebora.swecker.ui.alarm_browser

import MainCoroutineRule
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.alarm_browser.AlarmRepository
import dev.bebora.swecker.data.alarm_browser.FakeAlarmRepository
import dev.bebora.swecker.data.service.AccountsService
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.service.ChatService
import dev.bebora.swecker.data.service.impl.UserWithFriends
import dev.bebora.swecker.data.service.testimpl.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AlarmBrowserViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AlarmBrowserViewModel
    private lateinit var alarmProviderService: AlarmProviderService
    private lateinit var authService: AuthService
    private lateinit var accountsService: AccountsService
    private lateinit var repository: AlarmRepository
    private lateinit var chatService: ChatService

    @OptIn(ExperimentalCoroutinesApi::class)
    val dispatcher = StandardTestDispatcher()

    private val initialUserId = FakeAlarmRepository.currentUserId

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        authService = FakeAuthService(initialUserId = initialUserId)
        accountsService = FakeAccountsService(
            initialUsers = mutableMapOf(
                "luca" to UserWithFriends(
                    id = "luca",
                    name = "Luca",
                    username = "luca",
                    friends = listOf(
                        User(
                            id = "carm",
                            name = "Carm",
                            username = "carm"
                        )
                    )
                ),
                "carm" to UserWithFriends(
                    id = "carm",
                    name = "Carm",
                    username = "carm",
                    friends = listOf(
                        User(
                            id = "luca",
                            name = "Luca",
                            username = "carm"
                        )
                    )
                ),
                "me" to UserWithFriends(
                    id = "me",
                    name = "Me",
                    username = "me",
                    friends = emptyList()
                ),
                "you" to UserWithFriends(
                    id = "you",
                    name = "You",
                    username = "you",
                    friends = emptyList()
                )
            ),
            initialFriendshipRequests = mutableMapOf("luca" to listOf("me"))
        )
        alarmProviderService = FakeAlarmProviderService()
        repository = FakeAlarmRepository(fakeAlarmProviderService = alarmProviderService)
        chatService = FakeChatService()
        viewModel = AlarmBrowserViewModel(
            repository = repository,
            authService = authService,
            accountsService = accountsService,
            chatService = chatService,
            alarmProviderService = alarmProviderService,
            iODispatcher = dispatcher,
            application = null
        )
    }

    @Test
    fun alarmBrowserViewModel_NavBarNavigate_StateUpdated() {
        assertEquals(0, viewModel.uiState.usersData.size)
        NavBarDestination.values().forEach {
            viewModel.onEvent(
                AlarmBrowserEvent.NavBarNavigate(
                    it
                ))
            assertEquals(it, viewModel.uiState.selectedDestination)
        }
    }

    @Test
    fun alarmBrowserViewModel_PartiallyUpdateAlarm_StateUpdated() {
        val id = "verylegitid"
        val name = "Ding"
        val alarmType = AlarmType.CHANNEL
        viewModel.onEvent(AlarmBrowserEvent.AlarmPartiallyUpdated(
            alarm = Alarm(
                id = id,
                name = name,
                alarmType = alarmType
            )
        ))
        assertEquals(id, viewModel.uiState.selectedAlarm!!.id)
        assertEquals(name, viewModel.uiState.selectedAlarm!!.name)
        assertEquals(alarmType, viewModel.uiState.selectedAlarm!!.alarmType)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun alarmBrowserViewModel_UpdateAlarm_RepositoryReceiveRequest() = runBlocking {
        val id = "verylegitid"
        val name = "Ding"
        val alarmType = AlarmType.PERSONAL
        val oldAlarm = Alarm(
            id = id,
            name = name + "oldname",
            alarmType = alarmType
        )
        repository.insertAlarm(oldAlarm, userId = initialUserId)
        val originalAlarmsSize = repository.getAllAlarms().first().size
        val newAlarm = Alarm(
            id = id,
            name = name,
            alarmType = alarmType
        )
        viewModel.onEvent(AlarmBrowserEvent.AlarmPartiallyUpdated(
            alarm = newAlarm
        ))
        viewModel.onEvent(
            AlarmBrowserEvent.AlarmUpdated(
                alarm = newAlarm,
                success = true
            )
        )
        dispatcher.scheduler.advanceUntilIdle()
        val newAlarms = repository.getAllAlarms().first()
        // Alarms size is the same
        assertEquals(originalAlarmsSize, newAlarms.size)
        // Old alarm has been updated
        assertEquals(id, newAlarms[0].id)
        assertEquals(name, newAlarms[0].name)
        assertEquals(alarmType, newAlarms[0].alarmType)
    }
}
