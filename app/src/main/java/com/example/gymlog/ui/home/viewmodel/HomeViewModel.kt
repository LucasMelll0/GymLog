package com.example.gymlog.ui.home.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.gymlog.model.Training
import com.example.gymlog.repository.TrainingRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface HomeViewModel {

    val trainings: Flow<List<Training>>
    val filters: List<String>

    fun manageFilters(filter: String)

    suspend fun deleteTraining(trainingId: String)
    suspend fun sync()
}

class HomeViewModelImpl(private val repository: TrainingRepository) : HomeViewModel, ViewModel() {

    private val currentUser = Firebase.auth.currentUser

    override val trainings: Flow<List<Training>> = currentUser?.let {
        repository.getAll(it.uid)
    } ?: emptyFlow()

    private val _filters = mutableStateListOf<String>()
    override val filters: List<String> get() = _filters


    override fun manageFilters(filter: String) {
        if (!filters.contains(filter)) _filters.add(filter) else _filters.remove(filter)
    }

    override suspend fun deleteTraining(trainingId: String) {
        currentUser?.let { currentUser ->
            try {
                repository.getById(trainingId, currentUser.uid)?.let {
                    repository.disable(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override suspend fun sync() {
        currentUser?.let {
            repository.sync(it.uid)
        }
    }

}