package dev.bebora.swecker.ui.alarm_notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


fun scheduleExactAlarm(context: Context, dateTime: OffsetDateTime, name: String) {
    val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val alarmIntent = Intent(context, AlarmNotificationReceiver::class.java).let { intent ->
        intent.action = "ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED"
        intent.putExtra("DateTime", dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        intent.putExtra("Name", name)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
    alarmMgr!!.cancel(alarmIntent)

    if ((Build.VERSION.SDK_INT >= 31
                && alarmMgr.canScheduleExactAlarms())
    ) {
        alarmMgr.setExact(
            AlarmManager.RTC_WAKEUP, dateTime.toInstant().toEpochMilli(), alarmIntent
        )
    } else {
        alarmMgr.setExact(
            AlarmManager.RTC_WAKEUP, dateTime.toInstant().toEpochMilli(), alarmIntent
        )
    }
}
