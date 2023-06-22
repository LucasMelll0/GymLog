package com.example.gymlog.ui.bmi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymlog.model.BmiInfo
import com.example.gymlog.model.User
import com.example.gymlog.repository.BmiInfoRepository
import com.example.gymlog.repository.UserRepository
import com.example.gymlog.utils.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

class BmiHistoricViewModel(
    private val userRepository: UserRepository,
    private val bmiRepository: BmiInfoRepository
) : ViewModel() {


    private val _userResource: MutableStateFlow<Resource<User?>> =
        MutableStateFlow(Resource.Loading)
    internal val userResource: Flow<Resource<User?>> = _userResource

    private val currentUser = Firebase.auth.currentUser

    val getHistoric = currentUser?.let{ bmiRepository.getAll(it.uid) } ?: emptyFlow()

    fun setLoading() {
        _userResource.value = Resource.Loading
    }

    suspend fun saveUser(user: User) {
        currentUser?.let {
            userRepository.saveUser(user.copy(id = it.uid))
        }
    }

    suspend fun sync() {
        currentUser?.let {
            userRepository.sync(it.uid)
            bmiRepository.sync(it.uid)
        }
    }

    suspend fun getUser() {
        if (_userResource.value !is Resource.Success) {
            try {
                userRepository.getUser().collect { user ->
                    _userResource.value = Resource.Success(user)
                }
            } catch (e: Exception) {
                _userResource.value = Resource.Error("error on get user")
            }
        }
    }

    suspend fun disableBmiInfoRegister(bmiInfo: BmiInfo) {
        try {
            bmiRepository.disable(bmiInfo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}