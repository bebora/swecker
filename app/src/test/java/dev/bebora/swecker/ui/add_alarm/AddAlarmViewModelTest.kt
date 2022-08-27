package dev.bebora.swecker.ui.add_alarm

import MainCoroutineRule
import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.AlarmType
import dev.bebora.swecker.data.alarm_browser.AlarmRepository
import dev.bebora.swecker.data.alarm_browser.FakeAlarmRepository
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.testimpl.FakeAlarmProviderService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

class AddAlarmViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AddAlarmViewModel
    private lateinit var repository: AlarmRepository
    private lateinit var alarmProviderService: AlarmProviderService

    @OptIn(ExperimentalCoroutinesApi::class)
    val dispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        alarmProviderService = FakeAlarmProviderService()
        repository = FakeAlarmRepository(fakeAlarmProviderService = alarmProviderService)
        viewModel = AddAlarmViewModel(
            repository = repository,
            iODispatcher = dispatcher
        )
    }

    @Test
    fun addAlarmViewModel_ChangeScreenData_StateUpdated() {
        val initialAlarm = viewModel.vmAlarm
        val newData = Alarm(
            id = initialAlarm.id,
            name = "Very new name",
            localDate = LocalDate.MAX,
            localTime = LocalTime.MAX,
            dateTime = OffsetDateTime.now().minusDays(30),
            alarmType = AlarmType.CHANNEL,
            enableChat = !initialAlarm.enableChat
        )
        viewModel.onAlarmPartiallyUpdate(
            alarm = newData
        )
        assertEquals(newData.enableChat, viewModel.vmAlarm.enableChat)
        assertEquals(newData.id, viewModel.vmAlarm.id)
        assertEquals(newData.name, viewModel.vmAlarm.name)
        assertEquals(newData.localDate, viewModel.vmAlarm.localDate)
        assertEquals(newData.localTime, viewModel.vmAlarm.localTime)
        assertEquals(newData.alarmType, viewModel.vmAlarm.alarmType)
        assertEquals(newData.dateTime, viewModel.vmAlarm.dateTime)
    }

    @Test
    fun addAlarmViewModel_DiscardAlarmCreation_StateIsReset() {
        val newData = Alarm(
            id = "somethingreal",
            name = "Very new name",
            localDate = LocalDate.MAX,
            localTime = LocalTime.MAX,
            dateTime = OffsetDateTime.now().minusDays(30),
            alarmType = AlarmType.CHANNEL,
            enableChat = false
        )
        viewModel.onAlarmPartiallyUpdate(
            newData
        )
        viewModel.onUpdateCanceled()
        // Just test that the name is emptied, could be improved
        assertEquals("", viewModel.vmAlarm.name)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addAlarmViewModel_AddAlarm_RepositoryIsPopulated() = runBlocking {
        val uniqueId = "somethingveryrealtotallyunique"
        val newData = Alarm(
            id = uniqueId,
            name = "Very new name",
            localDate = LocalDate.MAX,
            localTime = LocalTime.MAX,
            dateTime = OffsetDateTime.now().minusDays(30),
            alarmType = AlarmType.PERSONAL,
            enableChat = false
        )
        viewModel.onUpdateCompleted(
            alarm = newData,
            success = true,
            group = null,
            userId = FakeAlarmRepository.currentUserId,
            alarmType = AlarmType.PERSONAL
        )
        dispatcher.scheduler.advanceUntilIdle()
        val currentAlarms = repository.getAllAlarms().first()
        val alarm = currentAlarms.first {
            it.id == uniqueId
        }
        assertNotNull(alarm)
    }

    @Test
    fun addAlarmViewModel_PressCancel_StateIsReset() {
        val newData = Alarm(
            id = "somethingreal",
            name = "Very new name",
            localDate = LocalDate.MAX,
            localTime = LocalTime.MAX,
            dateTime = OffsetDateTime.now().minusDays(30),
            alarmType = AlarmType.CHANNEL,
            enableChat = false
        )
        viewModel.onUpdateCompleted(
            alarm = newData,
            success = false,
            group = null,
            userId = FakeAlarmRepository.currentUserId,
            alarmType = AlarmType.PERSONAL
        )
        // Just test that the name is emptied, could be improved
        assertEquals("", viewModel.vmAlarm.name)
    }
}
