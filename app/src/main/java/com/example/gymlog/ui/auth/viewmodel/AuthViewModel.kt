package com.example.gymlog.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymlog.ui.auth.data.SignInResult
import com.example.gymlog.ui.auth.data.SignInState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    internal val state = _state.asStateFlow()
    internal val currentUser = Firebase.auth.currentUser

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}