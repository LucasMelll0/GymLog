package com.example.gymlog.ui.form.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gymlog.model.Exercise

class TrainingFormViewModel : ViewModel() {

    private var _trainingTitle by mutableStateOf("")
    val trainingTitle get() = _trainingTitle

    private val _exercises = mutableStateListOf<Exercise>()
    val exercises: List<Exercise> get() = _exercises

    fun setTrainingTitle(title: String) {
        _trainingTitle = title
    }

    fun addExercise(exercise: Exercise) {
        _exercises.add(exercise)
    }

    fun removeExercise(exercise: Exercise) {
        _exercises.remove(exercise)
    }
}