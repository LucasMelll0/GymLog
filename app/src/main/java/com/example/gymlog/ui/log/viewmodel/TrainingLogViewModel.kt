package com.example.gymlog.ui.log.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gymlog.model.ExerciseMutableState
import com.example.gymlog.model.Training
import com.example.gymlog.repository.TrainingRepository
import com.example.gymlog.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TrainingLogViewModel(private val repository: TrainingRepository) : ViewModel() {

    companion object {
        const val TAG = "trainingViewModel"
    }

    private var _title by mutableStateOf("")
    internal val title: String get() = _title

    private val _exercises = mutableStateListOf<ExerciseMutableState>()
    val exercises: List<ExerciseMutableState> get() = _exercises
    private val _filters = mutableStateListOf<String>()
    val filters: List<String> get() = _filters

    private val _resource: MutableStateFlow<Resource<Training>> = MutableStateFlow(Resource.Loading)
    internal val resource: Flow<Resource<Training>> = _resource


    fun setLoading() {
        _resource.value = Resource.Loading
    }
    suspend fun getTraining(id: String) {
        if (_resource.value !is Resource.Success) {
            _resource.value =
                try {
                    repository.getById(id)?.let { training ->
                        _title = training.title
                        _exercises.clear()
                        _exercises.addAll(training.getExercisesWithMutableState())
                        _filters.clear()
                        _filters.addAll(training.filters)
                        Resource.Success(training)
                    } ?: run {
                        Resource.Error("Error on get training: null pointer")
                    }

                } catch (e: Exception) {
                    Log.w(TAG, "getTraining: ", e)
                    Resource.Error("Error on get training")
                }
        }
    }

    fun updateExercise(exerciseId: String, isChecked: Boolean) {
        _exercises.find { it.id == exerciseId }?.let {
            it.isChecked = isChecked
        }
    }

    fun resetExercises() {
        _exercises.forEach {
            it.isChecked = false
        }
    }

    suspend fun removeTraining(trainingId: String) {
        repository.getById(trainingId)?.let { training ->
            this._resource.value = Resource.Loading
            repository.remove(training)
        }
    }

    suspend fun updateTraining(trainingId: String) {
        repository.getById(trainingId)?.let { training ->
            this._resource.value = Resource.Loading
            repository.save(training.copy(exercises = exercises.map { it.toExercise() }))
        }
    }

}