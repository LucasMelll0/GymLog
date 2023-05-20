package com.example.gymlog.ui.log.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymlog.model.Training
import com.example.gymlog.repository.TrainingRepository
import com.example.gymlog.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrainingLogViewModel(private val repository: TrainingRepository) : ViewModel() {

    internal var training: Flow<Resource<Training>> = flow { emit(Resource.Loading) }
        private set

    suspend fun getTraining(id: String) {
        training = flow {
            repository.getById(id)?.let {
                emit(Resource.Success(it))
            } ?: run {
                emit(Resource.Error("Failed"))
            }
        }
    }

}