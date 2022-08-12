package dev.bebora.swecker.ui.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

fun feedbackVibrationEnabled(context: Context) {
    val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= 31) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator;
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;
    }

    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
    } else {
        vibrator.vibrate(200);
    }
}
