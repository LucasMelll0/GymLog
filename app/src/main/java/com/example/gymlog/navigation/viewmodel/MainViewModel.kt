package com.example.gymlog.navigation.viewmodel

import android.app.Activity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.datastore.UserStore
import com.example.gymlog.ui.auth.authclient.AuthUiClient
import com.example.gymlog.ui.auth.authclient.UserCredentials
import com.example.gymlog.ui.auth.viewmodel.AuthViewModel
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

    fun signInWithIntent(
        result: ActivityResult,
        authViewModel: AuthViewModel,
    )

    fun signInWithGoogle(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>)

    fun signInWithEmailAndPassword(userCredentials: UserCredentials, authViewModel: AuthViewModel)

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

    override fun signInWithIntent(
        result: ActivityResult,
        authViewModel: AuthViewModel,
    ) {
        setIsLoadingTo(true)
        if (result.resultCode == Activity.RESULT_OK) {
            viewModelScope.launch {
                val signInResult = authClient.signInWithIntent(
                    intent = result.data ?: run {
                        setIsLoadingTo(false)
                        return@launch
                    }
                )
                signInResult.data?.googleIdToken?.let {
                    userStore.saveToken(it)
                }
                authViewModel.onSignInResult(signInResult)
            }
        }
        setIsLoadingTo(false)
    }

    override fun signInWithGoogle(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) {
        viewModelScope.launch {
            setIsLoadingTo(true)
            val signInIntentSender = authClient.signInWithGoogle()
            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender ?: return@launch
                ).build()
            )
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

}