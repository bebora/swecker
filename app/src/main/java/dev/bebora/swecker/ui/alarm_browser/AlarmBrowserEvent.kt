package dev.bebora.swecker.ui.alarm_browser

import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.Group

sealed class AlarmBrowserEvent {
    data class NavBarNavigate(val destination: NavBarDestination) : AlarmBrowserEvent()

    data class GroupSelected(val group: Group) : AlarmBrowserEvent()

    data class AlarmSelected(val alarm: Alarm) : AlarmBrowserEvent()

    data class AlarmUpdated(val alarm: Alarm, val success: Boolean) : AlarmBrowserEvent()

    data class AlarmPartiallyUpdated(val alarm: Alarm) : AlarmBrowserEvent()

    data class DialogOpened(val type: DialogContent) : AlarmBrowserEvent()

    data class DetailsOpened(val type: DetailsScreenContent) : AlarmBrowserEvent()

    data class Search(val key: String) : AlarmBrowserEvent()

    object BackButtonPressed : AlarmBrowserEvent()

    object OpenChatTEMP : AlarmBrowserEvent() //Remove this

    data class SendMessageTEMP(val text: String) : AlarmBrowserEvent()
}
