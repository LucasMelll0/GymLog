package com.example.gymlog.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.gymlog.R

@Composable
fun formatTime(timeInMillis: Long): String {
    val minutes = timeInMillis / 1000 / 60
    val seconds = timeInMillis / 1000 % 60
    val millis = timeInMillis % 1000
    return stringResource(id = R.string.stopwatch_face_time_format, minutes, seconds, millis)
}