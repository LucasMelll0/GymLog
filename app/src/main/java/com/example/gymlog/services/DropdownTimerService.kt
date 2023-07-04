package com.example.gymlog.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.gymlog.R
import com.example.gymlog.extensions.vibrate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DropdownTimerService : Service() {

    companion object {

        const val NOTIFICATION_ID = "dropdown_timer_notification"
        const val NOTIFICATION_NAME = "dropdown timer"
        private const val NOTIFICATION_INT_ID = 456

        private var countDownTimer: CountDownTimer? = null

        private val pIsRunning = MutableStateFlow(false)
        internal val isRunning: StateFlow<Boolean> get() = pIsRunning

        private val pStartTimeInMillis = MutableStateFlow(0.0f)
        internal val startTimeInMillis: StateFlow<Float> get() = pStartTimeInMillis

        private val pCurrentTimeInMillis = MutableStateFlow(0.0f)
        internal val currentTimeInMillis: StateFlow<Float> get() = pCurrentTimeInMillis

        fun start(context: Context) {
            countDownTimer = object : CountDownTimer(currentTimeInMillis.value.toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    pCurrentTimeInMillis.value -= 1000
                    createNotification(context = context)
                }

                override fun onFinish() {
                    context.vibrate()
                    reset(context)
                }
            }.start()
            pIsRunning.value = true
        }

        private fun createNotification(context: Context) {
            val minutes = currentTimeInMillis.value.toLong() / 1000 / 60
            val seconds = currentTimeInMillis.value.toLong() / 1000 % 60
            val formattedMinutes = if (minutes < 10) "0$minutes" else minutes.toString()
            val formattedSeconds = if (seconds < 10) "0$seconds" else seconds.toString()
            val notification = NotificationCompat.Builder(context, NOTIFICATION_ID)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(
                    context.getString(
                        R.string.app_timer_title,
                        "$formattedMinutes:$formattedSeconds"
                    )
                )
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
                .setAutoCancel(false)
                .build()
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.notify(456, notification)
        }

        fun pause() {
            countDownTimer?.cancel() ?: run { countDownTimer = null }
            pIsRunning.value = false
        }

        fun reset(context: Context) {
            pause()
            if (startTimeInMillis.value == currentTimeInMillis.value) {
                setStartInMillis(0f)
            } else {
                pCurrentTimeInMillis.value = startTimeInMillis.value
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.cancel(NOTIFICATION_INT_ID)
        }

        fun setStartInMillis(value: Float) {
            if (value >= 0) {
                pStartTimeInMillis.value = value
                pCurrentTimeInMillis.value = value
            }
        }

        fun increaseTime(value: Float) {
            if (startTimeInMillis.value + value <= 3600000) {
                pStartTimeInMillis.value += value
                pCurrentTimeInMillis.value += value
            }
        }

        fun decreaseTime(value: Float) {
            if (startTimeInMillis.value - value >= 0) {
                pStartTimeInMillis.value -= value
                pCurrentTimeInMillis.value -= value
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null


    override fun onDestroy() {
        super.onDestroy()
        pause()
        scope.cancel()
    }

}