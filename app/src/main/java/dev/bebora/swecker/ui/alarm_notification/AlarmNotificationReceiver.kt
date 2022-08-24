package dev.bebora.swecker.ui.alarm_notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


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

        val notificationIntent = Intent(context, NotificationActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra("DateTime", intent.getStringExtra("DateTime"))
            .putExtra("Name", intent.getStringExtra("Name"))
        context.startActivity(notificationIntent)
    }
}
