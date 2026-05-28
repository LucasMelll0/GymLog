package com.devmello.gymlog.ui.bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmello.gymlog.R
import com.devmello.gymlog.core.model.UserData
import com.devmello.gymlog.core.model.repositories.UserRepository
import com.devmello.gymlog.core.ui.LoadingManager
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.extensions.toUserData
import com.devmello.gymlog.model.BmiInfo
import com.devmello.gymlog.model.User
import com.devmello.gymlog.repository.BmiInfoRepository
import com.devmello.gymlog.services.NetworkMonitor
import com.devmello.gymlog.utils.State
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface BmiHistoricViewModel {

    val userState: Flow<State<User?>>
    val user: Flow<User?>
    val currentUser: UserData?
    val getHistoric: Flow<List<BmiInfo>>

    fun setLoading()
    fun setUser(user: User)
    fun saveUser(user: User, onSuccess: () -> Unit)
    fun sync()
    fun getUser()
    fun disableBmiInfoRegister(bmiInfo: BmiInfo, onFinished: () -> Unit)
}

class BmiHistoricViewModelImpl(
    private val userRepository: UserRepository,
    private val bmiRepository: BmiInfoRepository,
    private val loadingManager: LoadingManager,
    networkMonitor: NetworkMonitor,

    ) : BmiHistoricViewModel, ViewModel() {

    private val _userState: MutableStateFlow<State<User?>> =
        MutableStateFlow(State.Loading)
    override val userState: Flow<State<User?>> = _userState
    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    override val user: Flow<User?> = _user
    override val currentUser = Firebase.auth.currentUser?.toUserData()

    override val getHistoric = currentUser?.let { bmiRepository.getAll(it.uid) } ?: emptyFlow()

    init {
        val isConnected = networkMonitor.checkCurrentConnection()
        if (isConnected) {
            sync()
        }
        _user.value ?: getUser()
    }

    override fun setLoading() {
        _userState.value = State.Loading
    }

    override fun saveUser(user: User, onSuccess: () -> Unit) {
        loadingManager.show()
        viewModelScope.launch {
            currentUser?.let {
                userRepository.saveUser(user.copy(id = it.uid))
                onSuccess()
            }
            loadingManager.hide()
        }
    }

    override fun sync() {
        loadingManager.show(textId = R.string.common_synchronizing)
        viewModelScope.launch {
            currentUser?.let {
                userRepository.sync(it.uid)
                bmiRepository.sync(it.uid)
            }
            loadingManager.hide()
        }
    }

    override fun setUser(user: User) = _user.update { user }

    override fun getUser() {
        loadingManager.show()
        viewModelScope.launch {
            if (_userState.value !is State.Success) {
                currentUser?.let {
                    userRepository.getUser(it.uid).collect { user ->
                        _userState.value = State.Success(user)
                    }
                } ?: run {
                    _userState.value = State.Error("error on get user")
                }
            }
            loadingManager.hide()
        }
    }

    override fun disableBmiInfoRegister(bmiInfo: BmiInfo, onFinished: () -> Unit) {
        loadingManager.show()
        viewModelScope.launch {
            try {
                bmiRepository.disable(bmiInfo)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                onFinished()
                loadingManager.hide()
            }
        }
    }
}