package com.devmello.gymlog.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmello.gymlog.core.model.repositories.AuthRepository
import com.devmello.gymlog.core.model.AuthResult
import com.devmello.gymlog.core.model.UserCredentials
import com.devmello.gymlog.core.model.UserData
import com.devmello.gymlog.core.model.repositories.UserPreferencesRepository
import com.devmello.gymlog.core.ui.LoadingManager
import com.devmello.gymlog.core.ui.MessageDuration
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.ui.auth.authclient.SignInState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface AuthViewModel {

    val state: StateFlow<SignInState>
    val currentUser: UserData?

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
    private val messageManager: MessageManager
) : AuthViewModel, ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    override val state = _state.asStateFlow()

    override val currentUser
        get() = Firebase.auth.currentUser?.run {
            UserData(
                uid = uid,
                userName = displayName ?: "",
                profilePicture = photoUrl?.toString() ?: "",
            )
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
        if(result is AuthResult.Error) {
            val message = result.errorMessage
            messageManager.postMessage(message, duration = MessageDuration.INDEFINITE)
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