package dev.bebora.swecker.ui.add_channel

import MainCoroutineRule
import android.net.Uri
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.AccountsService
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.service.ImageStorageService
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAlarmProviderService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.data.service.testimpl.FakeImageStorageService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class AddChannelViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AddChannelViewModel
    private lateinit var alarmProviderService: AlarmProviderService
    private lateinit var authService: AuthService
    private lateinit var imageStorageService: FakeImageStorageService
    private lateinit var accountsService: AccountsService

    @OptIn(ExperimentalCoroutinesApi::class)
    val dispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        alarmProviderService = FakeAlarmProviderService()
        authService = FakeAuthService(initialUserId = "validuser")
        accountsService = FakeAccountsService(
            users = FakeAccountsService.defaultUsers.toMutableMap(),
            friendshipRequests = FakeAccountsService.defaultFriendshipRequests.toMutableMap()
        )
        imageStorageService = FakeImageStorageService()
        alarmProviderService = FakeAlarmProviderService()
        viewModel = AddChannelViewModel(
            authService = authService,
            accountsService = accountsService,
            imageStorageService = imageStorageService,
            alarmProviderService = alarmProviderService,
            iODispatcher = dispatcher
        )
    }

    @Test
    fun addChannelViewModel_SetTempName_StateUpdated() {
        val originalName = viewModel.uiState.channelName
        val newName = originalName+"TRUE"
        viewModel.setChannelName(newName)
        assertEquals(newName, viewModel.uiState.channelName)
    }

    @Test
    fun addChannelViewModel_SetTempHandle_StateUpdated() {
        val originalHandle = viewModel.uiState.channelHandle
        val newHandle = originalHandle+"longer"
        viewModel.setChannelHandle(newHandle)
        assertEquals(newHandle, viewModel.uiState.channelHandle)
    }

    @Test
    fun addChannelViewModel_SetPicture_StateUpdated() {
        val uri = Mockito.mock(Uri::class.java)
        viewModel.setChannelPic(uri)
        assertNotEquals("", viewModel.uiState.uploadedPicUrl)
    }

    @Test
    fun addChannelViewModel_ConfirmCreation_ChannelIsCreated() {
        val sampleString = "asdfghjkl"
        viewModel.setChannelName(sampleString)
        viewModel.setChannelName(sampleString)
        viewModel.confirmChannelCreation {
            alarmProviderService.searchNewChannels(
                from = User(id = "qwertyuiop"),
                query = sampleString.dropLast(2),
                onError = { assert(false) }
            ) {
                assertEquals(1, it.size)
                println("Test actually done")
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addChannelViewModel_DiscardCreation_StateIsReset() {
        val fakeName = "trebora"
        viewModel.setChannelName(fakeName)
        viewModel.discardChannelCreation {
            assertNotEquals(fakeName, viewModel.uiState.channelName)
            println("Test actually done")
        }
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, imageStorageService.deletedImages)
    }
}
