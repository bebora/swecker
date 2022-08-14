package dev.bebora.swecker.ui.utils

import android.util.Log

fun onError(error: Throwable) {
    Log.e("SWECKER-ERR", error.localizedMessage ?: error.toString())
}
