package com.example.gymlog.ui.log.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gymlog.model.Training
import com.example.gymlog.repository.TrainingRepository
import com.example.gymlog.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrainingLogViewModel(private val repository: TrainingRepository) : ViewModel() {

    companion object {
        const val TAG = "trainingViewModel"
    }

    internal var training: Flow<Resource<Training>> = flow { emit(Resource.Loading) }
        private set

    suspend fun getTraining(id: String) {
        training = flow {
            try {
                repository.getById(id).collect { training ->
                    training?.let {
                        emit(Resource.Success(training))
                    } ?: run {
                        emit(Resource.Error("Error on get training: null pointer"))
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "getTraining: ", e)
                emit(Resource.Error("Error on get training"))
            }
        }
    }

}