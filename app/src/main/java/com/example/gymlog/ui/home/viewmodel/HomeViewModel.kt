package com.example.gymlog.ui.home.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.model.Training
import com.example.gymlog.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val TAG = "HomeViewModel"

    internal val trainings: Flow<List<Training>> = repository.getAll()

    private val _filters = mutableStateListOf<String>()
    internal val filters: List<String> get() = _filters


    fun manageFilters(filter: String) {
        if (!filters.contains(filter)) _filters.add(filter) else _filters.remove(filter)
    }

    fun deleteTraining(trainingId: String) {
        viewModelScope.launch {
            try {
                repository.getById(trainingId)?.let {
                    repository.remove(it)
                }
            }catch (e: Exception) {
                Log.w(TAG, "deleteTraining: ", e)
            }
        }
    }

}