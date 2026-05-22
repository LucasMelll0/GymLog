package com.devmello.gymlog.ui.bmi.viewmodel

import androidx.lifecycle.ViewModel
import com.devmello.gymlog.core.model.UserData
import com.devmello.gymlog.core.model.repositories.UserRepository
import com.devmello.gymlog.extensions.toUserData
import com.devmello.gymlog.model.BmiInfo
import com.devmello.gymlog.model.User
import com.devmello.gymlog.repository.BmiInfoRepository
import com.devmello.gymlog.utils.State
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update

interface BmiHistoricViewModel {

    val userState: Flow<State<User?>>
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


    private val _userState: MutableStateFlow<State<User?>> =
        MutableStateFlow(State.Loading)
    override val userState: Flow<State<User?>> = _userState
    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    override val user: Flow<User?> = _user
    override val currentUser = Firebase.auth.currentUser?.toUserData()

    override val getHistoric = currentUser?.let { bmiRepository.getAll(it.uid) } ?: emptyFlow()

    override fun setLoading() {
        _userState.value = State.Loading
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
        if (_userState.value !is State.Success) {
            currentUser?.let {
                userRepository.getUser(it.uid).collect { user ->
                    _userState.value = State.Success(user)
                }
            } ?: run {
                _userState.value = State.Error("error on get user")
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