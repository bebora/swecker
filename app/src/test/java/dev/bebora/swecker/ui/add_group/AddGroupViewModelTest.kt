package dev.bebora.swecker.ui.add_group

import MainCoroutineRule
import android.net.Uri
import dev.bebora.swecker.data.User
import dev.bebora.swecker.data.service.AccountsService
import dev.bebora.swecker.data.service.AlarmProviderService
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.data.service.ImageStorageService
import dev.bebora.swecker.data.service.impl.UserWithFriends
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
import dev.bebora.swecker.data.service.testimpl.FakeAlarmProviderService
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.data.service.testimpl.FakeImageStorageService
import dev.bebora.swecker.ui.add_channel.AddChannelViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class AddGroupViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AddGroupViewModel
    private lateinit var alarmProviderService: AlarmProviderService
    private lateinit var authService: AuthService
    private lateinit var imageStorageService: FakeImageStorageService
    private lateinit var accountsService: AccountsService

    @OptIn(ExperimentalCoroutinesApi::class)
    val dispatcher = StandardTestDispatcher()

    val initialUserId = "luca"

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        alarmProviderService = FakeAlarmProviderService()
        authService = FakeAuthService(initialUserId = initialUserId)
        accountsService = FakeAccountsService(
            initialFriendshipRequests = mutableMapOf("luca" to listOf(FakeAuthService.validUserId))
        )
        imageStorageService = FakeImageStorageService()
        alarmProviderService = FakeAlarmProviderService()
        viewModel = AddGroupViewModel(
            authService = authService,
            accountsService = accountsService,
            imageStorageService = imageStorageService,
            alarmProviderService = alarmProviderService,
            iODispatcher = dispatcher
        )
    }

    @Test
    fun addGroupViewModel_SetTempName_StateUpdated() {
        val originalName = viewModel.uiState.groupName
        val newName = originalName + "TRUE"
        viewModel.setGroupName(newName)
        assertEquals(newName, viewModel.uiState.groupName)
    }

    @Test
    fun addGroupViewModel_ToggleContact_StateUpdated() = runBlocking {
        assertEquals(0, viewModel.uiState.selectedMembers.size)
        val friends = accountsService.getFriends("luca").first()
        val oneFriend = friends[0]
        viewModel.toggleContactSelection(oneFriend)
        assertEquals(1, viewModel.uiState.selectedMembers.size)
        viewModel.toggleContactSelection(oneFriend)
        assertEquals(0, viewModel.uiState.selectedMembers.size)
    }

    @Test
    fun addGroupViewModel_ConfirmMembersSelection_PartialGroupCreated() = runBlocking {
        val friends = accountsService.getFriends("luca").first()
        val oneFriend = friends[0]
        viewModel.toggleContactSelection(oneFriend)
        viewModel.nextScreen()
        assertEquals(2, viewModel.uiState.tempGroupData.members.size)
    }

    @Test
    fun addGroupViewModel_ReturnToContactsSelection_StateUpdated() = runBlocking {
        val friends = accountsService.getFriends("luca").first()
        val oneFriend = friends[0]
        viewModel.toggleContactSelection(oneFriend)
        viewModel.nextScreen()
        assertEquals(AddGroupContent.GROUP_SELECT_NAME, viewModel.uiState.content)
        viewModel.previousScreen()
        assertEquals(AddGroupContent.GROUP_SELECT_CONTACTS, viewModel.uiState.content)
    }

    @Test
    fun addGroupViewModel_SetPicture_ImageUploaded() = runBlocking {
        val friends = accountsService.getFriends("luca").first()
        val oneFriend = friends[0]
        viewModel.toggleContactSelection(oneFriend)
        viewModel.nextScreen()
        assertEquals(AddGroupContent.GROUP_SELECT_NAME, viewModel.uiState.content)
        val uri = Mockito.mock(Uri::class.java)
        viewModel.setGroupPic(uri)
        assertEquals(1, imageStorageService.uploadedImages)
    }

    @Test
    fun addGroupViewModel_ConfirmGroupCreation_GroupUpdated() = runBlocking {
        // Initially no groups
        val groups = alarmProviderService.getUserGroups(initialUserId).first()
        assertEquals(0, groups.size)
        alarmProviderService.getUserGroups(initialUserId).first()
        val friends = accountsService.getFriends("luca").first()
        val oneFriend = friends[0]
        viewModel.toggleContactSelection(oneFriend)
        viewModel.nextScreen()
        val name = "nail"
        viewModel.setGroupName(name)
        viewModel.confirmGroupCreation {
            runBlocking {
                val newGroups = alarmProviderService.getUserGroups(initialUserId).first()
                assertEquals(1, newGroups.size)
                println("Test actually done")
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addGroupViewModel_DiscardGroupCreation_StateIsReset() = runBlocking {
        val friends = accountsService.getFriends("luca").first()
        val oneFriend = friends[0]
        viewModel.toggleContactSelection(oneFriend)
        viewModel.nextScreen()
        val name = "nail"
        viewModel.setGroupName(name)
        viewModel.discardGroupCreation {
            assertNotEquals(name, viewModel.uiState.groupName)
            assertEquals(0, viewModel.uiState.selectedMembers.size)
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, imageStorageService.deletedImages)
            println("Test actually done")
        }
    }
}
