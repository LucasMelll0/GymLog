package com.example.gymlog.ui.form.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gymlog.model.Exercise
import com.example.gymlog.model.Training
import com.example.gymlog.repository.TrainingRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TrainingFormViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val currentUser = Firebase.auth.currentUser

    private var _trainingId: String? by mutableStateOf(null)

    private var _trainingTitle by mutableStateOf("")
    val trainingTitle get() = _trainingTitle

    private val _exercises = mutableStateListOf<Exercise>()
    val exercises: List<Exercise> get() = _exercises

    private val _filters = mutableStateListOf<String>()
    val filters: List<String> get() = _filters


    suspend fun getTrainingById(trainingId: String) {
        _trainingId ?: run {
            currentUser?.let {
                repository.getById(trainingId, it.uid)?.let { training ->
                    _trainingId = training.trainingId
                    _trainingTitle = training.title
                    _exercises.clear()
                    _exercises.addAll(training.exercises)
                    _filters.clear()
                    _filters.addAll(training.filters)
                }
            }
        }
    }

    fun setTrainingTitle(title: String) {
        _trainingTitle = title
    }

    fun addExercise(exercise: Exercise) {
        _exercises.add(exercise)
        exercise.filters.forEach {
            if (!filters.contains(it)) _filters.add(it)
        }

    }

    fun removeExercise(exercise: Exercise) {
        _exercises.find { it == exercise }?.let {
            _exercises.remove(it)
        }
        exercise.filters.forEach { filter ->
            _exercises.find { it.filters.contains(filter) } ?: _filters.remove(filter)
        }
    }

    suspend fun saveTraining(training: Training) {
        currentUser?.let {
            _trainingId?.let {
                repository.save(training.copy(trainingId = it))
            } ?: run {
                repository.save(training.copy(userId = it.uid))
            }
        }
    }
}