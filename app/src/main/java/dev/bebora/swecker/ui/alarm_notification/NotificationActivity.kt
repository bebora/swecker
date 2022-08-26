package dev.bebora.swecker.ui.alarm_notification

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.*
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.bebora.swecker.data.settings.Ringtone
import dev.bebora.swecker.data.settings.Settings
import dev.bebora.swecker.data.settings.SettingsRepositoryInterface
import dev.bebora.swecker.data.settings.toSeconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActivity(
) : ComponentActivity() {
    @Inject
    lateinit var repository: SettingsRepositoryInterface
    private lateinit var vibrator: Vibrator

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()
        super.onCreate(savedInstanceState)
        val dateTime = intent.getStringExtra("DateTime")
        val name = intent.getStringExtra("Name")

        vibrator = if (Build.VERSION.SDK_INT >= 31) {
            val vibratorManager =
                this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        lifecycleScope.launch {
            val result = repository.getSettings().first()
            onSettingsLoaded(result)
        }

        setContent {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val offsetDateTime = OffsetDateTime.parse(dateTime)
                AlarmNotificationFullScreen(
                    alarmName = name!!,
                    time = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        vibrator.cancel()
    }

    private fun onSettingsLoaded(settings: Settings) {
        val audioResource = when (settings.ringtone) {
            Ringtone.DEFAULT -> dev.bebora.swecker.R.raw.nuclear
            else -> dev.bebora.swecker.R.raw.nuclear
        }
        mediaPlayer = MediaPlayer.create(this, audioResource).apply {
            isLooping = true
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
            setVolume(settings.ringtoneVolume / 100f, settings.ringtoneVolume / 100f)
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        }

        lifecycleScope.launch {
            val ringtoneDurationMillis = settings.ringtoneDuration.toSeconds() * 1000L
            delay(timeMillis = ringtoneDurationMillis)
            mediaPlayer.stop()
        }

        mediaPlayer.start()

        if (settings.vibration) {
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(650, 350),
                        intArrayOf(0, 122),
                        0
                    )
                )
            } else {
                vibrator.vibrate(longArrayOf(650, 350), 0)
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
