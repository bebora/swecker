package dev.bebora.swecker.ui.alarm_browser

import MainCoroutineRule
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.alarm_browser.AlarmRepository
import dev.bebora.swecker.data.alarm_browser.FakeAlarmRepository
import dev.bebora.swecker.data.service.AccountsService
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.service.ChatService
import dev.bebora.swecker.data.service.testimpl.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

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
        accountsService = FakeAccountsService()
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
                )
            )
            assertEquals(it, viewModel.uiState.selectedDestination)
        }
    }

    @Test
    fun alarmBrowserViewModel_PartiallyUpdateAlarm_StateUpdated() {
        val id = "verylegitid"
        val name = "Ding"
        val alarmType = AlarmType.CHANNEL
        viewModel.onEvent(
            AlarmBrowserEvent.AlarmPartiallyUpdated(
                alarm = Alarm(
                    id = id,
                    name = name,
                    alarmType = alarmType
                )
            )
        )
        assertEquals(id, viewModel.uiState.selectedAlarm!!.id)
        assertEquals(name, viewModel.uiState.selectedAlarm!!.name)
        assertEquals(alarmType, viewModel.uiState.selectedAlarm!!.alarmType)
    }


    @Test
    fun alarmBrowserViewModel_SelectedGroup_StateUpdated() {
        val group = Group(
            id = "1",
            name = "testGroup",
            owner = "test"
        )

        val alarm = Alarm(
            id = UUID.randomUUID().toString(),
            name = "Test group alarm",
            alarmType = AlarmType.GROUP,
            groupId = "1"
        )

        val alarmNoGroup =
            Alarm(
                id = UUID.randomUUID().toString(),
                name = "Test alarm",
                alarmType = AlarmType.CHANNEL,
            )

        runBlocking {
            repository.insertAlarm(alarm = alarm, userId = initialUserId)
            repository.insertAlarm(alarm = alarmNoGroup, userId = initialUserId)
        }

        viewModel.onEvent(
            AlarmBrowserEvent.GroupSelected(
                group = group
            )
        )

        assertEquals(DetailsScreenContent.GROUP_ALARM_LIST, viewModel.uiState.detailsScreenContent)

        assertNotNull(viewModel.uiState.filteredAlarms)
        assertNotNull(viewModel.uiState.selectedGroup)

        assertEquals(group.id, viewModel.uiState.selectedGroup?.id)
        assertEquals(1, viewModel.uiState.filteredAlarms?.size)
        assertEquals(group.id, viewModel.uiState.filteredAlarms!![0].groupId)
    }

    @Test
    fun alarmBrowserViewModel_SelectedChannel_StateUpdated() {
        val channel = Group(
            id = "2",
            name = "testChannel",
            owner = "test"
        )

        val alarm = Alarm(
            id = UUID.randomUUID().toString(),
            name = "Test group alarm",
            alarmType = AlarmType.CHANNEL,
            groupId = "2"
        )

        val alarmNoChannel =
            Alarm(
                id = UUID.randomUUID().toString(),
                name = "Test alarm",
                alarmType = AlarmType.GROUP,
            )

        runBlocking {
            repository.insertAlarm(alarm = alarm, userId = initialUserId)
            repository.insertAlarm(alarm = alarmNoChannel, userId = initialUserId)
        }

        viewModel.onEvent(
            AlarmBrowserEvent.ChannelSelected(
                channel = channel
            )
        )

        assertEquals(DetailsScreenContent.CHANNEL_ALARM_LIST, viewModel.uiState.detailsScreenContent)

        assertNotNull(viewModel.uiState.filteredAlarms)
        assertNotNull(viewModel.uiState.selectedChannel)

        assertEquals(channel.id, viewModel.uiState.selectedChannel?.id)
        assertEquals(1, viewModel.uiState.filteredAlarms?.size)
        assertEquals(channel.id, viewModel.uiState.filteredAlarms!![0].groupId)
    }

    @Test
    fun alarmBrowserViewModel_SearchAlarm_UpdateState() {
        val alarmNotMatchingSearch = Alarm(
            id = UUID.randomUUID().toString(),
            name = "First name",
            alarmType = AlarmType.CHANNEL,
            groupId = "2"
        )

        val alarmMatchingSearch =
            Alarm(
                id = UUID.randomUUID().toString(),
                name = "Second Name",
                alarmType = AlarmType.GROUP,
            )

        runBlocking {
            repository.insertAlarm(alarm = alarmMatchingSearch, userId = initialUserId)
            repository.insertAlarm(alarm = alarmNotMatchingSearch, userId = initialUserId)
        }

        viewModel.onEvent(
            AlarmBrowserEvent.SearchAlarms(
                "sec"
            )
        )

        assertNotNull(viewModel.uiState.filteredAlarms)
        assertEquals(1,viewModel.uiState.filteredAlarms?.size)
        assertEquals(alarmMatchingSearch.name,viewModel.uiState.filteredAlarms!![0].name)
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
        viewModel.onEvent(
            AlarmBrowserEvent.AlarmPartiallyUpdated(
                alarm = newAlarm
            )
        )
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
