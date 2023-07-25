package com.example.gymlog.ui.bmi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymlog.extensions.toUserData
import com.example.gymlog.model.BmiInfo
import com.example.gymlog.model.User
import com.example.gymlog.repository.BmiInfoRepository
import com.example.gymlog.repository.UserRepository
import com.example.gymlog.ui.auth.authclient.UserData
import com.example.gymlog.utils.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update

interface BmiHistoricViewModel {

    val userResource: Flow<Resource<User?>>
    val user: Flow<User?>
    val currentUser: UserData?
    val getHistoric: Flow<List<BmiInfo>>

    fun setLoading()
    fun setUser(user: User)
    suspend fun saveUser(user: User)
    suspend fun sync()
    suspend fun getUser()
    suspend fun disableBmiInfoRegister(bmiInfo: BmiInfo)
}

class BmiHistoricViewModelImpl(
    private val userRepository: UserRepository,
    private val bmiRepository: BmiInfoRepository
) : BmiHistoricViewModel, ViewModel() {


    private val _userResource: MutableStateFlow<Resource<User?>> =
        MutableStateFlow(Resource.Loading)
    override val userResource: Flow<Resource<User?>> = _userResource
    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    override val user: Flow<User?> = _user
    override val currentUser = Firebase.auth.currentUser?.toUserData()

    override val getHistoric = currentUser?.let { bmiRepository.getAll(it.uid) } ?: emptyFlow()

    override fun setLoading() {
        _userResource.value = Resource.Loading
    }

    override suspend fun saveUser(user: User) {
        currentUser?.let {
            userRepository.saveUser(user.copy(id = it.uid))
        }
    }

    override suspend fun sync() {
        currentUser?.let {
            userRepository.sync(it.uid)
            bmiRepository.sync(it.uid)
        }
    }

    override fun setUser(user: User) = _user.update { user }

    override suspend fun getUser() {
        if (_userResource.value !is Resource.Success) {
            currentUser?.let {
                userRepository.getUser(it.uid).collect { user ->
                    _userResource.value = Resource.Success(user)
                }
            } ?: run {
                _userResource.value = Resource.Error("error on get user")
            }
        }
    }

    override suspend fun disableBmiInfoRegister(bmiInfo: BmiInfo) {
        try {
            bmiRepository.disable(bmiInfo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}