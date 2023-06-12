package com.example.gymlog.ui.bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.model.User
import com.example.gymlog.repository.BmiInfoRepository
import com.example.gymlog.repository.UserRepository
import com.example.gymlog.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class BmiHistoricViewModel(
    private val userRepository: UserRepository,
    private val bmiRepository: BmiInfoRepository
) : ViewModel() {


    private val _userResource: MutableStateFlow<Resource<User?>> = MutableStateFlow(Resource.Loading)
    internal val userResource: Flow<Resource<User?>> = _userResource

    val getHistoric = bmiRepository.getAll()

    fun setLoading() {
        _userResource.value = Resource.Loading
    }

    suspend fun saveUser(user: User) = userRepository.saveUser(user)



    fun getUser() {
        if (_userResource.value !is Resource.Success) {
            try {
                viewModelScope.launch {
                    userRepository.getUser().collect { user ->
                        _userResource.value = Resource.Success(user)
                    }
                }
            }catch (e: Exception) {
                _userResource.value = Resource.Error("error on get user")
            }
        }
    }

}