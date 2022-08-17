package dev.bebora.swecker.util

import androidx.annotation.StringRes
import dev.bebora.swecker.ui.utils.UiText

sealed class UiEvent {
    object PopBackStack : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data class ShowSnackbar(
        val uiText: UiText
    ) : UiEvent()
}
