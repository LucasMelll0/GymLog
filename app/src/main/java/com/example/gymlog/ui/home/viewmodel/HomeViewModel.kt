package com.example.gymlog.ui.home.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.gymlog.model.Training

class HomeViewModel : ViewModel() {

    private val _trainings = mutableStateListOf<Training>()
    internal val trainings: List<Training> get() = _trainings

    private val _filters = mutableStateListOf<String>()
    internal val filters: List<String> get() = _filters

    fun addAllTrainings(trainings: List<Training>) {
        _trainings.addAll(trainings)
    }

    fun manageFilters(filter: String) {
        if (!filters.contains(filter)) _filters.add(filter) else _filters.remove(filter)
    }

}