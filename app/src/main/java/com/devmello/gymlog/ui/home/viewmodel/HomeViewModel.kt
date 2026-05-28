package com.devmello.gymlog.ui.home.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmello.gymlog.R
import com.devmello.gymlog.core.ui.LoadingManager
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.model.Training
import com.devmello.gymlog.repository.TrainingRepository
import com.devmello.gymlog.services.NetworkMonitor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface HomeViewModel {

    val trainings: StateFlow<List<Training>>
    val filters: List<String>

    fun manageFilters(filter: String)

    fun deleteTraining(trainingId: String, onDeleted: () -> Unit)
    fun sync()
}

class HomeViewModelImpl(
    private val repository: TrainingRepository,
    private val loadingManager: LoadingManager,
    private val messageManager: MessageManager,
    networkMonitor: NetworkMonitor
) : HomeViewModel, ViewModel() {

    private val currentUser = Firebase.auth.currentUser

    private val _trainings: MutableStateFlow<List<Training>> = MutableStateFlow(emptyList())

    override val trainings: StateFlow<List<Training>> = _trainings.asStateFlow()


    init {
        loadingManager.show()
        currentUser?.let {
            getTrainings(it.uid)
        } ?: loadingManager.hide()
        val isConnected = networkMonitor.checkCurrentConnection()
        if (isConnected) {
            sync()
        }
    }

    private fun getTrainings(userId: String) {
        viewModelScope.launch {
            repository.getAll(userId).collect { trainings ->
                _trainings.value = trainings
            }
        }
        loadingManager.hide()
    }

    private val _filters = mutableStateListOf<String>()
    override val filters: List<String> get() = _filters


    override fun manageFilters(filter: String) {
        if (!filters.contains(filter)) _filters.add(filter) else _filters.remove(filter)
    }

    override fun deleteTraining(trainingId: String, onDeleted: () -> Unit) {
        loadingManager.show()
        viewModelScope.launch {
            currentUser?.let { currentUser ->
                try {
                    repository.getById(trainingId, currentUser.uid)?.let {
                        repository.disable(it)
                    }
                    onDeleted()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            loadingManager.hide()
        }
    }

    override fun sync() {
        viewModelScope.launch {
            currentUser?.let {
                repository.sync(it.uid)
            }
        }
    }

}