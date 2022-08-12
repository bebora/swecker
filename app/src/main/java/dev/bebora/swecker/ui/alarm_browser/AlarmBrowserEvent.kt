package dev.bebora.swecker.ui.alarm_browser

import dev.bebora.swecker.data.Alarm

sealed class AlarmBrowserEvent {
    data class NavBarNavigate(val destination: NavBarDestination) : AlarmBrowserEvent()
    data class GroupSelected(val groupId: Long) : AlarmBrowserEvent()

    data class AlarmSelected(val alarm: Alarm) : AlarmBrowserEvent()

    data class AlarmUpdated(val alarm: Alarm, val success: Boolean) : AlarmBrowserEvent()

    data class AlarmPartiallyUpdated(val alarm: Alarm) : AlarmBrowserEvent()
}