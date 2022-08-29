package dev.bebora.swecker.ui.alarm_notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dev.bebora.swecker.R
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


private const val TAG = "AlarmNotificationReceiver"

class AlarmNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        /*StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(TAG, log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }*/

        /*val notificationIntent = Intent(context, NotificationActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra("DateTime", intent.getStringExtra("DateTime"))
            .putExtra("Name", intent.getStringExtra("Name"))
        context.startActivity(notificationIntent)*/

        val fullScreenIntent = Intent(context, NotificationActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra("DateTime", intent.getStringExtra("DateTime"))
            .putExtra("Name", intent.getStringExtra("Name"))
        
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder =
            NotificationCompat.Builder(context, "swecker__01")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(true)
                .setContentTitle(intent.getStringExtra("Name"))
                .setContentText(
                    OffsetDateTime.parse(intent.getStringExtra("DateTime")).atZoneSameInstant(
                        ZoneId.systemDefault()
                    )
                        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
                )
                .setVibrate(longArrayOf(650, 350))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent, true)

        val alarmNotification = notificationBuilder.build()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, alarmNotification)
    }
}
