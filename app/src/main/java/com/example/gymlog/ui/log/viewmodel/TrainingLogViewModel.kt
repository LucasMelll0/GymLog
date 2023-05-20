package com.example.gymlog.ui.log.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.gymlog.model.ExerciseMutableState
import com.example.gymlog.model.Training
import com.example.gymlog.repository.TrainingRepository
import com.example.gymlog.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrainingLogViewModel(private val repository: TrainingRepository) : ViewModel() {

    companion object {
        const val TAG = "trainingViewModel"
    }

    private val _exercises = mutableStateListOf<ExerciseMutableState>()
    val exercises: List<ExerciseMutableState> get() = _exercises

    internal var training: Flow<Resource<Training>> = flow { emit(Resource.Loading) }
        private set

    suspend fun getTraining(id: String) {
        training = flow {
            try {
                repository.getById(id)?.let { training ->
                    emit(Resource.Success(training))
                    _exercises.clear()
                    _exercises.addAll(training.getExercisesWithMutableState())
                } ?: run {
                    emit(Resource.Error("Error on get training: null pointer"))
                }

            } catch (e: Exception) {
                Log.w(TAG, "getTraining: ", e)
                emit(Resource.Error("Error on get training"))
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
            this.training = flow {
                emit(Resource.Loading)
            }
            repository.remove(training)
        }
    }

    suspend fun updateTraining(trainingId: String) {
        repository.getById(trainingId)?.let { training ->
            this.training = flow { emit(Resource.Loading) }
            repository.save(training.copy(exercises = exercises.map { it.toExercise() }))
        }
    }

}