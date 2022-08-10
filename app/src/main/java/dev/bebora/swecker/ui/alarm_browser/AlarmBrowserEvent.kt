package dev.bebora.swecker.ui.alarm_browser

sealed class AlarmBrowserEvent {
    data class NavBarNavigate(val destination: NavBarDestination) : AlarmBrowserEvent()
    data class GroupClicked(val groupId: Long) : AlarmBrowserEvent()
}
