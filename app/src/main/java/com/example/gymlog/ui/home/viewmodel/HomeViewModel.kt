package com.example.gymlog.ui.home.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.gymlog.model.Training
import com.example.gymlog.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow

class HomeViewModel(repository: TrainingRepository) : ViewModel() {

    internal val trainings: Flow<List<Training>> = repository.getAll()

    private val _filters = mutableStateListOf<String>()
    internal val filters: List<String> get() = _filters


    fun manageFilters(filter: String) {
        if (!filters.contains(filter)) _filters.add(filter) else _filters.remove(filter)
    }

}