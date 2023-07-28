package com.example.gymlog.ui.stopwatch.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

interface StopwatchViewModel {

    val savedTimes: List<Long>

    fun saveTime(time: Long)
    fun reset()

}

class StopwatchViewModelImpl() : StopwatchViewModel,
    ViewModel() {

    private val _savedTimes = mutableStateListOf<Long>()
    override val savedTimes: List<Long> get() = _savedTimes

    override fun saveTime(time: Long) {
        _savedTimes.add(time)
    }

    override fun reset() = _savedTimes.clear()


}