package com.example.gymlog.ui.home.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.model.Training
import com.example.gymlog.repository.TrainingRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val currentUser = Firebase.auth.currentUser

    internal val trainings: Flow<List<Training>> = currentUser?.let {
        repository.getAll(it.uid)
    } ?: emptyFlow()

    private val _filters = mutableStateListOf<String>()
    internal val filters: List<String> get() = _filters


    fun manageFilters(filter: String) {
        if (!filters.contains(filter)) _filters.add(filter) else _filters.remove(filter)
    }

    fun deleteTraining(trainingId: String) {
        viewModelScope.launch {
            currentUser?.let { currentUser ->
                try {
                    repository.getById(trainingId, currentUser.uid)?.let {
                        repository.disable(it)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "deleteTraining: ", e)
                }
            }
        }
    }

    suspend fun sync() {
        currentUser?.let {
            repository.sync(it.uid)
        }
    }

}