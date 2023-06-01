package com.example.gymlog.extensions

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

fun Context.vibrate(repetitions: Int = 5) {
    val timings = mutableListOf(0L).apply {
        for (i in 1..repetitions * 2) {
            add(500)
        }
    }.toLongArray()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibrator = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrator.defaultVibrator.vibrate(
            VibrationEffect
                .createWaveform(timings, -1)
        )
    } else {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.vibrate(
            VibrationEffect
                .createWaveform(timings, -1)
        )
    }
}