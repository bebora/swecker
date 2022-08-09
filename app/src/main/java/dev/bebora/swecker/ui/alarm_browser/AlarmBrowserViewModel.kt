package dev.bebora.swecker.ui.alarm_browser

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bebora.swecker.data.AlarmRepository
import javax.inject.Inject

@HiltViewModel
class AlarmBrowserViewModel  @Inject constructor(
    private val repository: AlarmRepository
): ViewModel() {
    //TODO add ViewModel logic
}
