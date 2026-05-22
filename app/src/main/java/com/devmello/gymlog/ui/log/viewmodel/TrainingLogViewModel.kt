package com.devmello.gymlog.ui.log.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmello.gymlog.extensions.toUserData
import com.devmello.gymlog.model.ExerciseMutableState
import com.devmello.gymlog.model.Training
import com.devmello.gymlog.repository.TrainingRepository
import com.devmello.gymlog.utils.State
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

interface TrainingLogViewModel {
    val title: String
    val exercises: List<ExerciseMutableState>
    val filters: List<String>
    val state: Flow<State<Training>>
    val savedStopwatchTimes: List<Long>
    fun setLoading()
    suspend fun getTraining(id: String)
    fun updateExercise(exerciseId: String, isChecked: Boolean)
    fun resetExercises()
    fun removeTraining(trainingId: String)
    fun updateTraining(trainingId: String)
    fun saveStopwatchTime(time: Long)
    fun resetStopwatchTimes()
}

class TrainingLogViewModelImpl(private val repository: TrainingRepository) : TrainingLogViewModel,
    ViewModel() {

    private val currentUser = Firebase.auth.currentUser?.toUserData()

    private var _title by mutableStateOf("")
    override val title: String get() = _title

    private val _exercises = mutableStateListOf<ExerciseMutableState>()
    override val exercises: List<ExerciseMutableState> get() = _exercises
    private val _filters = mutableStateListOf<String>()
    override val filters: List<String> get() = _filters
    private val _state: MutableStateFlow<State<Training>> = MutableStateFlow(State.Loading)
    override val state: Flow<State<Training>> = _state
    private val _savedStopwatchTimes = mutableStateListOf<Long>()
    override val savedStopwatchTimes: List<Long> get() = _savedStopwatchTimes


    override fun setLoading() {
        _state.value = State.Loading
    }

    override fun onCleared() {
        Log.i("TAG", "onCleared: aqui")
        super.onCleared()
    }

    override suspend fun getTraining(id: String) {
        if (_state.value !is State.Success) {
            _state.value =
                try {
                    currentUser?.let {
                        repository.getById(id, it.uid)?.let { training ->
                            _title = training.title
                            _exercises.clear()
                            _exercises.addAll(training.getExercisesWithMutableState())
                            _filters.clear()
                            _filters.addAll(training.filters)
                            State.Success(training)
                        } ?: run {
                            State.Error("Error on get training: null pointer")
                        }
                    } ?: State.Error("Error on get current user")

                } catch (_: Exception) {
                    State.Error("Error on get training")
                }
        }
    }

    override fun updateExercise(exerciseId: String, isChecked: Boolean) {
        _exercises.find { it.id == exerciseId }?.let {
            it.isChecked = isChecked
        }
    }

    override fun resetExercises() {
        _exercises.forEach {
            it.isChecked = false
        }
    }

    override fun removeTraining(trainingId: String) {
        viewModelScope.launch {
            currentUser?.let {
                repository.getById(trainingId, it.uid)?.let { training ->
                    _state.value = State.Loading
                    repository.disable(training)
                }
            }
        }
    }

    override fun updateTraining(trainingId: String) {
        viewModelScope.launch {
            currentUser?.let { currentUser ->
                repository.getById(trainingId, currentUser.uid)?.let { training ->
                    _state.value = State.Loading
                    repository.save(
                        training.copy(
                            exercises = exercises.map { it.toExercise() },
                            isSynchronized = false
                        )
                    )
                }
            }
        }

    }

    override fun saveStopwatchTime(time: Long) {
        _savedStopwatchTimes.add(time)
    }

    override fun resetStopwatchTimes() = _savedStopwatchTimes.clear()


}