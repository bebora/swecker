package dev.bebora.swecker.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.util.SETTINGS
import dev.bebora.swecker.util.SPLASH
import dev.bebora.swecker.data.service.AuthService
import dev.bebora.swecker.ui.utils.onError
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authService: AuthService,
) : ViewModel() {
    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        /*
        if (accountService.hasUser()) openAndPopUp(SETTINGS, SPLASH)
        else createAnonymousAccount(openAndPopUp)
         */
        openAndPopUp(SETTINGS, SPLASH)
    }

    /*private fun createAnonymousAccount(openAndPopUp: (String, String) -> Unit) {
        viewModelScope.launch {
            authService.createAnonymousAccount { error ->
                if (error != null) onError(error = error)
                else openAndPopUp(SETTINGS, SPLASH)
            }
        }
    }*/
}
