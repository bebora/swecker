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
        intent.putExtra("DateTime", dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        intent.putExtra("Name", name)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    alarmMgr!!.cancel(alarmIntent)

    if ((Build.VERSION.SDK_INT >= 31
                && alarmMgr.canScheduleExactAlarms())
    ) {
        alarmMgr.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, dateTime.toInstant().toEpochMilli(), alarmIntent
        )
    } else if (Build.VERSION.SDK_INT <= 31) {
        alarmMgr.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, dateTime.toInstant().toEpochMilli(), alarmIntent
        )
    }
}

fun cancelAlarm(context: Context) {
    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val alarmIntent = Intent(context, AlarmNotificationReceiver::class.java).let { intent ->
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
    }
    if (alarmIntent != null && alarmManager != null) {
        alarmManager.cancel(alarmIntent)
    }
}
