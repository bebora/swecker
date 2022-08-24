package dev.bebora.swecker.util

import androidx.annotation.StringRes
import dev.bebora.swecker.ui.utils.UiText

sealed class UiEvent {
    data class ShowSnackbar(
        val uiText: UiText
    ) : UiEvent()
    object VibrationFeedback : UiEvent()
}
