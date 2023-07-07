package com.example.gymlog.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "userInfo")
suspend fun Context.checkConnection(
    onNotConnected: suspend () -> Unit = {},
    onConnected: suspend () -> Unit
) {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork

    val activeNetwork = connectivityManager.getNetworkCapabilities(network)

    activeNetwork?.let {
        when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> onConnected()

            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> onConnected()

            else -> onNotConnected()
        }
    } ?: onNotConnected()
}

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