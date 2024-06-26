package dev.bebora.swecker.ui.alarm_browser

import dev.bebora.swecker.data.Alarm
import dev.bebora.swecker.data.Group

sealed class AlarmBrowserEvent {
    data class NavBarNavigate(val destination: NavBarDestination) : AlarmBrowserEvent()

    data class GroupSelected(val group: Group) : AlarmBrowserEvent()

    data class AlarmSelected(val alarm: Alarm) : AlarmBrowserEvent()

    data class ChannelSelected(val channel: Group) : AlarmBrowserEvent()

    data class AlarmUpdated(val alarm: Alarm, val success: Boolean) : AlarmBrowserEvent()

    data class AlarmDeleted(val alarm: Alarm) : AlarmBrowserEvent()

    data class AlarmPartiallyUpdated(val alarm: Alarm) : AlarmBrowserEvent()

    data class DialogOpened(val type: DialogContent) : AlarmBrowserEvent()

    data class DetailsOpened(val type: DetailsScreenContent) : AlarmBrowserEvent()

    data class SearchAlarms(val key: String) : AlarmBrowserEvent()

    data class SearchGroups(val key: String) : AlarmBrowserEvent()

    object BackButtonPressed : AlarmBrowserEvent()

    object OnTransitionCompleted : AlarmBrowserEvent()

    data class SendMessage(val text: String) : AlarmBrowserEvent()

    data class MessageValueChanged(val text: String) : AlarmBrowserEvent()

    data class JoinChannel(val channel: Group) : AlarmBrowserEvent()
}
