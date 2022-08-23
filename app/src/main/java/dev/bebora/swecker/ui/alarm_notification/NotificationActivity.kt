package dev.bebora.swecker.ui.alarm_notification

import android.os.*
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class NotificationActivity(
) : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()
        super.onCreate(savedInstanceState)
        val dateTime = intent.getStringExtra("DateTime")
        val name = intent.getStringExtra("Name")
        setContent {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val offsetDateTime = OffsetDateTime.parse(dateTime)
                AlarmNotificationFullScreen(
                    alarmName = name!!,
                    time = offsetDateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
                    onAlarmDismiss = {
                        finish()
                    },
                    onAlarmSnooze = {
                        scheduleExactAlarm(
                            context = this,
                            dateTime = offsetDateTime.plusMinutes(5),
                            name = name
                        )
                        finish()
                    })
            }
        }
    }

    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}
