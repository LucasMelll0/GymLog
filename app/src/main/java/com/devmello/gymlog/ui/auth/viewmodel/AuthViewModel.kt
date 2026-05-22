package com.devmello.gymlog.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmello.gymlog.core.model.AuthResult
import com.devmello.gymlog.core.model.UserCredentials
import com.devmello.gymlog.core.model.UserData
import com.devmello.gymlog.core.model.repositories.AuthRepository
import com.devmello.gymlog.core.model.repositories.UserPreferencesRepository
import com.devmello.gymlog.core.navigation.NavMethod
import com.devmello.gymlog.core.navigation.NavigationManager
import com.devmello.gymlog.core.ui.LoadingManager
import com.devmello.gymlog.core.ui.MessageDuration
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.extensions.toUserData
import com.devmello.gymlog.navigation.NavRoute
import com.devmello.gymlog.ui.auth.authclient.SignInState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface AuthViewModel {

    val state: StateFlow<SignInState>
    val currentUser: StateFlow<UserData?>

    fun onSignInResult(result: AuthResult)

    fun resetState()

    fun signInWithGoogle(alreadyRegistered: Boolean)

    fun signInWithEmailAndPassword(userCredentials: UserCredentials)

    fun registerWithEmailAndPassword(userCredentials: UserCredentials)

    fun sendPasswordResetEmail(email: String)

    fun signOut()

}

class AuthViewModelImpl(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val loadingManager: LoadingManager,
    private val messageManager: MessageManager,
    private val navigationManager: NavigationManager
) : AuthViewModel, ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    override val state = _state.asStateFlow()

    private val _currentUser = MutableStateFlow(Firebase.auth.currentUser?.toUserData())
    override val currentUser = _currentUser.asStateFlow()

    private val authStateListener: FirebaseAuth.AuthStateListener =
        FirebaseAuth.AuthStateListener { state ->
            _currentUser.value = state.currentUser?.toUserData()
        }

    init {
        Firebase.auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        Firebase.auth.removeAuthStateListener(authStateListener)
        super.onCleared()
    }

    override fun onSignInResult(result: AuthResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result is AuthResult.Success,
                signInError = if (result is AuthResult.Error) {
                    result.errorMessage
                } else null
            )
        }
        when (result) {
            is AuthResult.Success -> navigationManager.navigate(NavRoute.Home, NavMethod.INCLUSIVE)

            is AuthResult.Error -> result.errorMessage.let {
                messageManager.postMessage(it, duration = MessageDuration.LONG)
            }
        }

    }

    override fun resetState() {
        loadingManager.show()
        _state.update { SignInState() }
        viewModelScope.launch {
            userPreferencesRepository.clearToken()
            loadingManager.hide()
        }
    }

    override fun signInWithGoogle(
        alreadyRegistered: Boolean
    ) {
        viewModelScope.launch {
            loadingManager.show()
            val signInResult = authRepository.signInWithGoogle(alreadyRegistered)
            if (signInResult is AuthResult.Success) {
                signInResult.data.googleIdToken?.let {
                    userPreferencesRepository.saveToken(it)
                }
            }
            loadingManager.hide()
        }
    }

    override fun signInWithEmailAndPassword(
        userCredentials: UserCredentials,
    ) {
        viewModelScope.launch {
            loadingManager.show()
            val signInResult = authRepository.signInWithEmailAndPassword(userCredentials)
            onSignInResult(signInResult)
            loadingManager.hide()
        }
    }

    override fun registerWithEmailAndPassword(userCredentials: UserCredentials) {
        viewModelScope.launch {
            val registerResult = authRepository.registerWithEmailAndPassword(userCredentials)
            onSignInResult(registerResult)

        }
    }

    override fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            loadingManager.show()
            authRepository.sendPasswordResetEmail(email)
            loadingManager.hide()
        }
    }

    override fun signOut() {
        viewModelScope.launch {
            loadingManager.show()
            authRepository.signOutUser()
            resetState()
            loadingManager.hide()
        }
    }
}