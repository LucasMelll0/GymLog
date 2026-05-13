package com.devmello.gymlog.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.devmello.gymlog.ui.auth.authclient.AuthResult
import com.devmello.gymlog.ui.auth.authclient.SignInState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    internal val state = _state.asStateFlow()
    internal val currentUser = Firebase.auth.currentUser

    fun onSignInResult(result: AuthResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result is AuthResult.Success,
                signInError = if (result is AuthResult.Error) {
                    result.errorMessage
                } else null
            )
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}