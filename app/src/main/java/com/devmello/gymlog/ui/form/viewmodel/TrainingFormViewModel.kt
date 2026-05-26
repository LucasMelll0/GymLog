package com.devmello.gymlog.ui.form.viewmodel


import com.devmello.gymlog.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmello.gymlog.core.ui.LoadingManager
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.model.Exercise
import com.devmello.gymlog.model.Training
import com.devmello.gymlog.repository.TrainingRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface TrainingFormViewModel {
    val trainingTitle: String

    val hasErrors: StateFlow<Boolean>

    val nameHasError: Boolean

    val exercises: List<Exercise>

    val filters: List<String>

    fun getTrainingById(trainingId: String)

    fun setTrainingTitle(title: String)

    fun addExercise(exercise: Exercise)

    fun removeExercise(exercise: Exercise)

    fun saveTraining(onSuccess: () -> Unit)
}

class TrainingFormViewModelImpl(
    private val repository: TrainingRepository,
    private val loadingManager: LoadingManager,
    private val messageManager: MessageManager
) : TrainingFormViewModel,
    ViewModel() {

    private val currentUser = Firebase.auth.currentUser

    private var _trainingId: String? by mutableStateOf(null)

    private val _hasErrors = flow {
        emit(nameHasError)
    }

    override val nameHasError: Boolean get() = trainingTitle.isEmpty()

    override val hasErrors: StateFlow<Boolean>
        get() = _hasErrors.stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = WhileSubscribed()
        )
    private var _trainingTitle by mutableStateOf("")
    override val trainingTitle get() = _trainingTitle

    private val _exercises = mutableStateListOf<Exercise>()
    override val exercises: List<Exercise> get() = _exercises

    private val _filters = mutableStateListOf<String>()
    override val filters: List<String> get() = _filters


    override fun getTrainingById(trainingId: String) {
        _trainingId ?: run {
            loadingManager.show()
            viewModelScope.launch {
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
                loadingManager.hide()
            }
        }
    }

    override fun setTrainingTitle(title: String) {
        _trainingTitle = title
    }

    override fun addExercise(exercise: Exercise) {
        _exercises.add(exercise)
        exercise.filters.forEach {
            if (!filters.contains(it)) _filters.add(it)
        }

    }

    override fun removeExercise(exercise: Exercise) {
        _exercises.find { it == exercise }?.let {
            _exercises.remove(it)
        }
        exercise.filters.forEach { filter ->
            _exercises.find { it.filters.contains(filter) } ?: _filters.remove(filter)
        }
    }

    override fun saveTraining(onSuccess: () -> Unit) {
        loadingManager.show(textId = R.string.common_saving)
        val training = Training(
            title = trainingTitle,
            filters = filters,
            exercises = exercises
        )
        viewModelScope.launch {
            currentUser?.let { currentUser ->
                _trainingId?.let {
                    repository.save(training.copy(trainingId = it, userId = currentUser.uid))
                } ?: run {
                    repository.save(training.copy(userId = currentUser.uid))
                }
            } ?: run {
                loadingManager.hide()
                messageManager.postMessage(textId = R.string.common_error_message)
                return@launch
            }
            loadingManager.hide()
            messageManager.postMessage(textId = R.string.training_form_saved_with_success)
            onSuccess()
        }

    }
}