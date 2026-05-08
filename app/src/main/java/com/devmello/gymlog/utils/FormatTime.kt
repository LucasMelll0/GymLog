package com.devmello.gymlog.utils

import android.content.Context
import com.devmello.gymlog.R

fun formatTime(context: Context, timeInMillis: Long): String {
    val minutes = timeInMillis / 1000 / 60
    val seconds = timeInMillis / 1000 % 60
    val millis = timeInMillis % 1000
    return context.getString(R.string.stopwatch_face_time_format, minutes, seconds, millis)
}