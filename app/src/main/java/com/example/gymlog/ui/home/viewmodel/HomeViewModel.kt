package com.example.gymlog.ui.home.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.gymlog.model.Training

class HomeViewModel : ViewModel() {

    private val _trainings = mutableStateListOf<Training>()
    val trainings: List<Training> get() = _trainings

    fun addAllTrainings(trainings: List<Training>) {
        _trainings.addAll(trainings)
    }
}