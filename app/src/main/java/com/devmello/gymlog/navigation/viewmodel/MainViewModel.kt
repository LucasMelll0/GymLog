package com.devmello.gymlog.navigation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmello.gymlog.core.model.AuthResult
import com.devmello.gymlog.core.ui.LoadingManager
import com.devmello.gymlog.data.datastore.UserStore
import com.devmello.gymlog.ui.auth.authclient.AuthUiClient
import com.devmello.gymlog.ui.auth.authclient.UserCredentials
import com.devmello.gymlog.ui.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface MainViewModel {
    val isLoading: StateFlow<Boolean>
    val showExitConfirmationDialog: StateFlow<Boolean>
    fun setExitConfirmationDialogVisibility(value: Boolean)
}

class MainViewModelImpl(private val loadingManager: LoadingManager) :
    MainViewModel, ViewModel() {
    override val isLoading: StateFlow<Boolean>
        get() = loadingManager.isLoading

    override val showExitConfirmationDialog: StateFlow<Boolean>
        get() = _showExitConfirmationDialog

    private val _showExitConfirmationDialog = MutableStateFlow(false)

    override fun setExitConfirmationDialogVisibility(value: Boolean) {
        _showExitConfirmationDialog.value = value
    }

}