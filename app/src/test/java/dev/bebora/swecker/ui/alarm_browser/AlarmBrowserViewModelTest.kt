package dev.bebora.swecker.ui.alarm_browser

import MainCoroutineRule
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.Group
import dev.bebora.swecker.data.ThinGroup
import dev.bebora.swecker.data.alarm_browser.AlarmRepository
import dev.bebora.swecker.data.alarm_browser.FakeAlarmRepository
import dev.bebora.swecker.data.service.AccountsService
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.service.ChatService
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAlarmProviderService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.data.service.testimpl.FakeChatService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime
import java.util.*

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
    fun alarmBrowserViewModel_DeletedAlarm_StateUpdated() {
        val alarm = Alarm(
            id = UUID.randomUUID().toString(),
            name = "Test group alarm",
            alarmType = AlarmType.CHANNEL,
            groupId = "2"
        )

        val alarmToDelete =
            Alarm(
                id = UUID.randomUUID().toString(),
                name = "Test alarm",
                alarmType = AlarmType.GROUP,
            )

        runBlocking {
            repository.insertAlarm(alarm = alarm, userId = initialUserId)
            repository.insertAlarm(alarm = alarmToDelete, userId = initialUserId)
        }

        viewModel.onEvent(
            AlarmBrowserEvent.AlarmDeleted(
                alarm = alarmToDelete
            )
        )

        assertNull(viewModel.uiState.selectedAlarm)

        //do not show screen that requires a selectedAlarm
        assertNotEquals(DetailsScreenContent.ALARM_DETAILS, viewModel.uiState.detailsScreenContent)
        assertNotEquals(DetailsScreenContent.CHAT, viewModel.uiState.detailsScreenContent)


        assertEquals(1, viewModel.uiState.alarms.size)
        assertNotEquals(alarmToDelete.id, viewModel.uiState.alarms[0].id)
    }

    @Test
    fun alarmBrowserViewModel_DetailSelection_StateUpdated() {
        val testDetailsScreenContent = DetailsScreenContent.GROUP_ALARM_LIST

        viewModel.onEvent(AlarmBrowserEvent.DetailsOpened(testDetailsScreenContent))

        assertEquals(testDetailsScreenContent, viewModel.uiState.detailsScreenContent)
    }
    @Test
    fun alarmBrowserViewModel_DialogSelection_StateUpdated() {
        val testDialogContent = DialogContent.ADD_ALARM

        viewModel.onEvent(AlarmBrowserEvent.DialogOpened(testDialogContent))

        assertEquals(testDialogContent, viewModel.uiState.dialogContent)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun alarmBrowserViewModel_JoinChannel_StateUpdated() {
        val channel = Group(
            id = "2",
            name = "testChannel",
            handle = "test",
            owner = "test",
            members = listOf("test")
        )
        val alarm = Alarm(
            id = UUID.randomUUID().toString(),
            groupId = "2",
            name = "channelTestAlarm",
            dateTime = OffsetDateTime.now(),
            alarmType = AlarmType.CHANNEL
        )
        runBlocking {
            alarmProviderService.createChannel(
                channel = ThinGroup(
                    id = channel.id,
                    name = channel.name,
                    lowerName = channel.name,
                    handle = channel.handle,
                    owner = channel.owner,
                    members = channel.members
                ),
                onComplete = {}
            )
            repository.insertAlarm(alarm = alarm, userId = null)
            viewModel.onEvent(AlarmBrowserEvent.JoinChannel(channel))
        }

        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.channels.size)
        assertEquals(channel.copy(
            members = channel.members.plus(viewModel.uiState.me.id)
        ), viewModel.uiState.channels.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun alarmBrowserViewModel_SearchChannel_StateUpdated() {
        val channel = Group(
            id = "2",
            name = "toFind",
            handle = "toFind",
            owner = "toFindOwner",
            members = listOf("toFindOwner")
        )
        val channelMine = Group(
            id = "3",
            name = "testChannelMine",
            handle = "testMine",
            owner = viewModel.uiState.me.id,
            members = listOf(viewModel.uiState.me.id)
        )

        runBlocking {
            alarmProviderService.createChannel(
                channel = ThinGroup(
                    id = channel.id,
                    name = channel.name,
                    lowerName = channel.name,
                    handle = channel.handle,
                    owner = channel.owner,
                    members = channel.members
                ),
                onComplete = {}
            )
            alarmProviderService.createChannel(
                channel = ThinGroup(
                    id = channelMine.id,
                    name = channelMine.name,
                    lowerName = channelMine.name,
                    handle = channelMine.handle,
                    owner = channelMine.owner,
                    members = channelMine.members
                ),
                onComplete = {}
            )
            viewModel.onEvent(AlarmBrowserEvent.SearchGroups("toFind"))
        }

        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.channels.size)
        assertEquals(1, viewModel.uiState.extraChannels.size)
        assertEquals(channel, viewModel.uiState.extraChannels.first())
    }

    @Test
    fun alarmBrowserViewModel_GoBack_PathRespected() {
        var testDetailsScreenContent = DetailsScreenContent.GROUP_ALARM_LIST

        viewModel.onEvent(AlarmBrowserEvent.DetailsOpened(testDetailsScreenContent))
        viewModel.onEvent(AlarmBrowserEvent.BackButtonPressed)

        assertEquals(DetailsScreenContent.NONE, viewModel.uiState.detailsScreenContent)

        testDetailsScreenContent = DetailsScreenContent.CHANNEL_ALARM_LIST

        viewModel.onEvent(AlarmBrowserEvent.DetailsOpened(testDetailsScreenContent))
        viewModel.onEvent(AlarmBrowserEvent.BackButtonPressed)

        assertEquals(DetailsScreenContent.NONE,viewModel.uiState.detailsScreenContent)

        testDetailsScreenContent = DetailsScreenContent.CHANNEL_DETAILS

        viewModel.onEvent(AlarmBrowserEvent.DetailsOpened(testDetailsScreenContent))
        viewModel.onEvent(AlarmBrowserEvent.BackButtonPressed)

        assertEquals(DetailsScreenContent.CHANNEL_ALARM_LIST,viewModel.uiState.detailsScreenContent)

        testDetailsScreenContent = DetailsScreenContent.GROUP_DETAILS

        viewModel.onEvent(AlarmBrowserEvent.DetailsOpened(testDetailsScreenContent))
        viewModel.onEvent(AlarmBrowserEvent.BackButtonPressed)

        assertEquals(DetailsScreenContent.GROUP_ALARM_LIST,viewModel.uiState.detailsScreenContent)
    }

    @Test
    fun alarmBrowserViewModel_SelectedAlarm_StateUpdated() {
        val alarm = Alarm(
            id = UUID.randomUUID().toString(),
            name = "Test group alarm",
            alarmType = AlarmType.GROUP,
            groupId = "1"
        )

        val alarmPersonal =
            Alarm(
                id = UUID.randomUUID().toString(),
                name = "Test alarm",
                alarmType = AlarmType.PERSONAL,
            )

        viewModel.onEvent(
            AlarmBrowserEvent.AlarmSelected(
               alarm = alarmPersonal
            )
        )

        //personal alarms always open details screen
        assertNotNull(viewModel.uiState.selectedAlarm)
        assertEquals(DetailsScreenContent.ALARM_DETAILS, viewModel.uiState.detailsScreenContent)

        viewModel.onEvent(
            AlarmBrowserEvent.AlarmSelected(
                alarm = alarm
            )
        )
        assertNotNull(viewModel.uiState.selectedAlarm)
        assertEquals(DetailsScreenContent.CHAT,viewModel.uiState.detailsScreenContent )

        viewModel.onEvent(
            AlarmBrowserEvent.AlarmSelected(
                alarm = alarm.copy(enableChat = false)
            )
        )
        assertEquals(DetailsScreenContent.ALARM_DETAILS, viewModel.uiState.detailsScreenContent )
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
