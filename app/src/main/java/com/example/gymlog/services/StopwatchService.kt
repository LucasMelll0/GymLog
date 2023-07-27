package com.example.gymlog.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.common.base.Stopwatch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit

class StopwatchService : Service() {

    companion object {
        private var stopWatch: Stopwatch? = null
        private val _isRunning = MutableStateFlow(stopWatch?.isRunning ?: false)
        val isRunning: StateFlow<Boolean> get() = _isRunning
        val currentTime: Flow<Long> = flow {
            while (true) {
                delay(10)
                stopWatch?.let {
                    emit(it.elapsed(TimeUnit.MILLISECONDS))
                }
            }
        }

        fun start() {
            stopWatch?.start() ?: run {
                stopWatch = Stopwatch.createStarted()
            }
            _isRunning.update { true }
        }

        fun pause() {
            stopWatch?.stop()
            _isRunning.update { false }
        }

        fun reset() {
            stopWatch?.reset()
            _isRunning.update { false }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }

    override fun onDestroy() {
        stopWatch = null
    }
}