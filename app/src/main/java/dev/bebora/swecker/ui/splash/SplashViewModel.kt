package dev.bebora.swecker.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.SETTINGS
import dev.bebora.swecker.SPLASH
import dev.bebora.swecker.data.service.AccountService
import dev.bebora.swecker.ui.utils.onError
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService,
) : ViewModel() {
    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        if (accountService.hasUser()) openAndPopUp(SETTINGS, SPLASH)
        else createAnonymousAccount(openAndPopUp)
    }

    private fun createAnonymousAccount(openAndPopUp: (String, String) -> Unit) {
        viewModelScope.launch {
            accountService.createAnonymousAccount { error ->
                if (error != null) onError(error = error)
                else openAndPopUp(SETTINGS, SPLASH)
            }
        }
    }
}
