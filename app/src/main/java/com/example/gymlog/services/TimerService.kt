package com.example.gymlog.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import com.example.gymlog.extensions.vibrate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerService : Service() {

    companion object {

        private var countDownTimer: CountDownTimer? = null

        private val pIsRunning = MutableStateFlow(false)
        internal val isRunning: StateFlow<Boolean> get() = pIsRunning

        private val pStartTimeInMillis = MutableStateFlow(0f)
        internal val startTimeInMillis: StateFlow<Float> get() = pStartTimeInMillis

        private val pCurrentTimeInMillis = MutableStateFlow(0f)
        internal val currentTimeInMillis: StateFlow<Float> get() = pCurrentTimeInMillis

        fun start(context: Context) {
            countDownTimer = object : CountDownTimer(currentTimeInMillis.value.toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    pCurrentTimeInMillis.value -= 1000
                }

                override fun onFinish() {
                    context.vibrate()
                    reset()
                }
            }.start()
            pIsRunning.value = true
        }


        fun pause() {
            countDownTimer?.cancel() ?: run { countDownTimer = null }
            pIsRunning.value = false
        }

        fun reset() {
            pause()
            if (startTimeInMillis.value == currentTimeInMillis.value) {
                setStartInMillis(0f)
            } else {
                pCurrentTimeInMillis.value = startTimeInMillis.value
            }
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