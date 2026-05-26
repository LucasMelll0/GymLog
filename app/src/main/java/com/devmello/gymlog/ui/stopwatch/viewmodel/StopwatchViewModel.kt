package com.devmello.gymlog.ui.stopwatch.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmello.gymlog.services.StopwatchService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface StopwatchViewModel {

    val savedTimes: List<Long>

    fun saveTime(time: Long)
    fun reset()

}

class StopwatchViewModelImpl : StopwatchViewModel,
    ViewModel() {

    override var savedTimes by mutableStateOf<List<Long>>(emptyList())
        private set

    init {
        StopwatchService.savedTimes.onEach {
            savedTimes = it
        }.launchIn(viewModelScope)
    }

    override fun saveTime(time: Long) {
        StopwatchService.saveTime(time)
    }

    override fun reset() {
        // Note: Resetting from UI usually handled by StopwatchService.reset(context)
        // but if called here, we should ensure it's cleared in the service too if that's the intention.
        // However, AppStopwatch calls StopwatchService.reset(context) directly.
    }


}
