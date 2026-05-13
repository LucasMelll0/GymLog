package com.devmello.gymlog.navigation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmello.gymlog.data.datastore.UserStore
import com.devmello.gymlog.ui.auth.authclient.AuthResult
import com.devmello.gymlog.ui.auth.authclient.AuthUiClient
import com.devmello.gymlog.ui.auth.authclient.UserCredentials
import com.devmello.gymlog.ui.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface MainViewModel {
    val isLoading: StateFlow<Boolean>
    val showExitConfirmationDialog: StateFlow<Boolean>
    val userStore: UserStore
    val authClient: AuthUiClient

    fun setIsLoadingTo(value: Boolean)

    fun setExitConfirmationDialogVisibility(value: Boolean)

    fun signInWithGoogle(alreadyRegistered: Boolean = true)

    fun signInWithEmailAndPassword(userCredentials: UserCredentials, authViewModel: AuthViewModel)

    fun signOut()
}

class MainViewModelImpl(override val userStore: UserStore, override val authClient: AuthUiClient) :
    MainViewModel, ViewModel() {
    override val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _isLoading = MutableStateFlow(false)
    override val showExitConfirmationDialog: StateFlow<Boolean>
        get() = _showExitConfirmationDialog

    private val _showExitConfirmationDialog = MutableStateFlow(false)
    override fun setIsLoadingTo(value: Boolean) {
        _isLoading.value = value
    }

    override fun setExitConfirmationDialogVisibility(value: Boolean) {
        _showExitConfirmationDialog.value = value
    }

    override fun signInWithGoogle(alreadyRegistered: Boolean) {
        viewModelScope.launch {
            setIsLoadingTo(true)
            val signInResult = authClient.signInWithGoogle(alreadyRegistered)
            if (signInResult is AuthResult.Success) {
                signInResult.data.googleIdToken?.let {
                    userStore.saveToken(it)
                }
            }
            setIsLoadingTo(false)
        }
    }

    override fun signInWithEmailAndPassword(
        userCredentials: UserCredentials,
        authViewModel: AuthViewModel
    ) {
        viewModelScope.launch {
            setIsLoadingTo(true)
            val signInResult = authClient.signInWithEmailAndPassword(userCredentials)
            authViewModel.onSignInResult(signInResult)
            setIsLoadingTo(false)
        }
    }

    override fun signOut() {
        viewModelScope.launch {
            authClient.signOutUser()
        }
    }

}