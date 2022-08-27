package dev.bebora.swecker.ui.sign_up

import MainCoroutineRule
import dev.bebora.swecker.R
import dev.bebora.swecker.common.isValidEmail
import dev.bebora.swecker.common.isValidPassword
import dev.bebora.swecker.data.service.testimpl.FakeAccountsService
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

class SignUpViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SignUpViewModel
    private val authService = FakeAuthService()
    private val accountsService = FakeAccountsService()

    @Before
    fun setUp() {
        viewModel = SignUpViewModel(
            authService = authService,
            accountsService = accountsService
        )
    }

    @Test
    fun signUpViewModel_SetTempValues_StateUpdated() {
        val tempMail = "a@b.dev"
        val tempPassword = "kangaroos"

        viewModel.onEvent(SignUpEvent.SetTempEmail(tempMail))
        assertEquals(tempMail, viewModel.uiState.email)

        viewModel.onEvent(SignUpEvent.SetTempPassword(tempPassword))
        assertEquals(tempPassword, viewModel.uiState.password)
    }

    @Test
    fun signUpViewModel_SignUpWithInvalidEmail_UiIsNotified() =
        runBlocking {
            // Set temp values
            val tempMail = FakeAuthService.invalidSignupEmail
            assertFalse(tempMail.isValidEmail())
            val tempPassword = FakeAuthService.validPassword
            viewModel.onEvent(SignUpEvent.SetTempEmail(tempMail))
            viewModel.onEvent(SignUpEvent.SetTempPassword(tempPassword))

            val channel = viewModel.uiEvent
            viewModel.onEvent(
                SignUpEvent.SignUpClick(onNavigate = {})
            )
            assertEquals(
                R.string.invalid_email,
                ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
            )
        }

    @Test
    fun signUpViewModel_SignUpWithInvalidPassword_UiIsNotified() =
        runBlocking {
            // Set temp values
            val tempMail = FakeAuthService.validLoginEmail
            val tempPassword = FakeAuthService.invalidSignupPassword
            assertFalse(tempPassword.isValidPassword())
            viewModel.onEvent(SignUpEvent.SetTempEmail(tempMail))
            viewModel.onEvent(SignUpEvent.SetTempPassword(tempPassword))

            val channel = viewModel.uiEvent
            viewModel.onEvent(
                SignUpEvent.SignUpClick(onNavigate = {})
            )
            assertEquals(
                R.string.invalid_password,
                ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
            )
        }

    @Test
    fun signUpViewModel_SignUpWithAlreadyUsedEmail_UiIsNotified() =
        runBlocking {
            // Set temp values
            val tempMail = FakeAuthService.validLoginEmail
            val tempPassword = FakeAuthService.validPassword
            viewModel.onEvent(SignUpEvent.SetTempEmail(tempMail))
            viewModel.onEvent(SignUpEvent.SetTempPassword(tempPassword))

            val channel = viewModel.uiEvent
            viewModel.onEvent(
                SignUpEvent.SignUpClick(onNavigate = {})
            )
            assertEquals(
                R.string.email_already_exists,
                ((channel.first() as UiEvent.ShowSnackbar).uiText as UiText.StringResource).resId
            )
        }

    @Test
    fun signUpViewModel_SignInWithValidData_NavigationOccurs() {
        // Set temp values
        val tempMail = FakeAuthService.validSignupEmail
        val tempPassword = FakeAuthService.validPassword
        viewModel.onEvent(SignUpEvent.SetTempEmail(tempMail))
        viewModel.onEvent(SignUpEvent.SetTempPassword(tempPassword))

        var navigationOccurred = false
        viewModel.onEvent(
            SignUpEvent.SignUpClick(onNavigate = { navigationOccurred = true })
        )
        assertEquals(true, navigationOccurred)
    }
}
