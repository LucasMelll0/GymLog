package com.devmello.gymlog.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.devmello.gymlog.MainActivity
import com.devmello.gymlog.R
import com.devmello.gymlog.navigation.StopwatchDestination
import com.google.common.base.Stopwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit

class StopwatchService : Service() {

    companion object {
        const val NOTIFICATION_ID = "stopwatch_notification"
        const val NOTIFICATION_NAME = "stopwatch"
        private const val NOTIFICATION_INT_ID = 789

        private var stopWatch: Stopwatch? = null
        private val _isRunning = MutableStateFlow(stopWatch?.isRunning == true)
        val isRunning: StateFlow<Boolean> get() = _isRunning

        private val _elapsedTime = MutableStateFlow(0L)
        val currentTime: Flow<Long> = flow {
            while (true) {
                delay(10)
                stopWatch?.let {
                    val time = it.elapsed(TimeUnit.MILLISECONDS)
                    _elapsedTime.value = time
                    emit(time)
                }
            }
        }

        private val _savedTimes = MutableStateFlow<List<Long>>(emptyList())
        val savedTimes: StateFlow<List<Long>> get() = _savedTimes

        fun saveTime(time: Long) {
            _savedTimes.update { it + time }
        }

        fun start(context: Context) {
            stopWatch?.start() ?: run {
                stopWatch = Stopwatch.createStarted()
            }
            _isRunning.update { true }
            createStopwatchNotification(context)
        }

        fun pause(context: Context) {
            stopWatch?.stop()
            _isRunning.update { false }
            createStopwatchNotification(context)
        }

        fun reset(context: Context) {
            stopWatch?.reset()
            _isRunning.update { false }
            _elapsedTime.value = 0L
            _savedTimes.value = emptyList()
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.cancel(NOTIFICATION_INT_ID)
        }

        private fun createStopwatchNotification(context: Context) {
            val clickIntent = Intent(
                Intent.ACTION_VIEW,
                "gymlog://${StopwatchDestination.route}".toUri(),
                context,
                MainActivity::class.java
            )
            val clickPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(clickIntent)
                getPendingIntent(NOTIFICATION_INT_ID, FLAG_IMMUTABLE)
            }

            val elapsed = _elapsedTime.value
            val hours = TimeUnit.MILLISECONDS.toHours(elapsed)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsed) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsed) % 60
            
            val formattedTime = if (hours > 0) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }

            val notification = NotificationCompat.Builder(context, NOTIFICATION_ID)
                .setSmallIcon(R.drawable.ic_stopwatch)
                .setContentTitle(
                    context.getString(
                        R.string.stopwatch_notification_title,
                        formattedTime
                    )
                )
                .setContentText(
                    context.getString(
                        R.string.stopwatch_back_from_notification
                    )
                )
                .setOngoing(_isRunning.value)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setContentIntent(clickPendingIntent)
                .build()

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.notify(NOTIFICATION_INT_ID, notification)
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        currentTime.onEach {
            if (_isRunning.value) {
                createStopwatchNotification(this)
            }
        }.launchIn(scope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }

    override fun onDestroy() {
        scope.cancel()
        stopWatch = null
    }
}
