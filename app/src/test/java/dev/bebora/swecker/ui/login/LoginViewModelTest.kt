package dev.bebora.swecker.ui.login

import MainCoroutineRule
import dev.bebora.swecker.R
import dev.bebora.swecker.common.isValidEmail
import dev.bebora.swecker.common.isValidPassword
import dev.bebora.swecker.data.service.testimpl.FakeAuthService
import dev.bebora.swecker.ui.utils.UiText
import dev.bebora.swecker.util.UiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: LoginViewModel
    private val authService = FakeAuthService()

    @Before
    fun setUp() {
        viewModel = LoginViewModel(
            authService = authService
        )
    }

    @Test
    fun loginViewModel_SetTempValues_StateUpdated() {
        val tempMail = "a@b.dev"
        val tempPassword = "kangaroos"

        viewModel.onEvent(LoginEvent.SetTempEmail(tempMail))
        assertEquals(tempMail, viewModel.uiState.email)

        viewModel.onEvent(LoginEvent.SetTempPassword(tempPassword))
        assertEquals(tempPassword, viewModel.uiState.password)
    }

    @Test
    fun loginViewModel_SignInWithInvalidEmail_UiIsNotified() =
        runBlocking {
            // Set temp values
            val tempMail = FakeAuthService.invalidSignupEmail
            assertFalse(tempMail.isValidEmail())
            val tempPassword = FakeAuthService.validPassword
            viewModel.onEvent(LoginEvent.SetTempEmail(tempMail))
            viewModel.onEvent(LoginEvent.SetTempPassword(tempPassword))

            val channel = viewModel.uiEvent
            viewModel.onEvent(
                LoginEvent.SignInClick(onNavigate = {})
            )
            assertEquals(
                R.string.invalid_email,
                ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
            )
        }

    @Test
    fun loginViewModel_SignInWithInvalidPassword_UiIsNotified() =
        runBlocking {
            // Set temp values
            val tempMail = FakeAuthService.validLoginEmail
            val tempPassword = FakeAuthService.invalidSignupPassword
            assertFalse(tempPassword.isValidPassword())
            viewModel.onEvent(LoginEvent.SetTempEmail(tempMail))
            viewModel.onEvent(LoginEvent.SetTempPassword(tempPassword))

            val channel = viewModel.uiEvent
            viewModel.onEvent(
                LoginEvent.SignInClick(onNavigate = {})
            )
            assertEquals(
                R.string.invalid_password,
                ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
            )
        }

    @Test
    fun loginViewModel_SignInWithEmailNotFound_UiIsNotified() =
        runBlocking {
            // Set temp values
            val tempMail = FakeAuthService.disabledUserEmail
            val tempPassword = FakeAuthService.validPassword
            viewModel.onEvent(LoginEvent.SetTempEmail(tempMail))
            viewModel.onEvent(LoginEvent.SetTempPassword(tempPassword))

            val channel = viewModel.uiEvent
            viewModel.onEvent(
                LoginEvent.SignInClick(onNavigate = {})
            )
            assertEquals(
                R.string.invalid_user,
                ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
            )
        }

    @Test
    fun loginViewModel_SignInWithWrongPassword_UiIsNotified() =
        runBlocking {
            // Set temp values
            val tempMail = FakeAuthService.validLoginEmail
            val tempPassword = FakeAuthService.wrongPassword
            viewModel.onEvent(LoginEvent.SetTempEmail(tempMail))
            viewModel.onEvent(LoginEvent.SetTempPassword(tempPassword))

            val channel = viewModel.uiEvent
            viewModel.onEvent(
                LoginEvent.SignInClick(onNavigate = {})
            )
            assertEquals(
                R.string.wrong_password,
                ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
            )
        }

    @Test
    fun loginViewModel_SignInWithValidData_NavigationOccurs() {
        // Set temp values
        val tempMail = FakeAuthService.validLoginEmail
        val tempPassword = FakeAuthService.validPassword
        viewModel.onEvent(LoginEvent.SetTempEmail(tempMail))
        viewModel.onEvent(LoginEvent.SetTempPassword(tempPassword))

        var navigationOccurred = false
        viewModel.onEvent(
            LoginEvent.SignInClick(onNavigate = { navigationOccurred = true })
        )
        assertEquals(true, navigationOccurred)
    }
}
