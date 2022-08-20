package dev.bebora.swecker.ui.alarm_notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.*


private const val TAG = "AlarmNotificationReceiver"

class AlarmNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //TODO add actual music and vibration
        val wakeLock: PowerManager.WakeLock =
            (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                    acquire(10 * 60 * 1000L /*10 minutes*/)
                }
            }

        if (Build.VERSION.SDK_INT >= 31) {
            val vibrator =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(
                        2000,
                        5
                    )
                )
            )
        }

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


        wakeLock.release()
    }
}
