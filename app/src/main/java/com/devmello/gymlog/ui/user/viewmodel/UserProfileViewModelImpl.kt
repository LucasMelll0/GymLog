package com.devmello.gymlog.ui.user.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmello.gymlog.R
import com.devmello.gymlog.core.model.Response
import com.devmello.gymlog.core.model.UserData
import com.devmello.gymlog.core.model.repositories.AccountRepository
import com.devmello.gymlog.core.model.repositories.UserPreferencesRepository
import com.devmello.gymlog.core.model.repositories.UserRepository
import com.devmello.gymlog.core.ui.LoadingManager
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.repository.BmiInfoRepository
import com.devmello.gymlog.repository.TrainingRepository
import com.devmello.gymlog.services.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


interface UserProfileViewModel {
    val user: StateFlow<UserData?>
    val userProvider: String?

    fun changeUsername(username: String, onFailed: () -> Unit = {})

    fun changeUserPhoto(uri: Uri, onFailed: () -> Unit = {})

    fun changePassword(
        oldPassword: String, newPassword: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    )

    fun deleteUser(
        password: String, onSuccess: () -> Unit, onFailed: () -> Unit
    )

}

class UserProfileViewModelImpl(
    private val accountRepository: AccountRepository,
    private val trainingRepository: TrainingRepository,
    private val bmiInfoRepository: BmiInfoRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
    private val loadingManager: LoadingManager,
    private val networkMonitor: NetworkMonitor,
    private val messageManager: MessageManager
) : UserProfileViewModel, ViewModel() {

    private val _user = MutableStateFlow(accountRepository.currentUser)
    override val user: StateFlow<UserData?> get() = _user

    override val userProvider = accountRepository.userProvider

    override fun changeUsername(
        username: String, onFailed: () -> Unit
    ) {
        if(!networkMonitor.isOnline) {
            messageManager.postMessage(R.string.common_offline_message)
            return
        }
        loadingManager.show()
        viewModelScope.launch {
            val response = accountRepository.updateUsername(username)
            loadingManager.hide()
            if (response is Response.Success) reload() else onFailed()
        }

    }

    override fun changeUserPhoto(
        uri: Uri, onFailed: () -> Unit
    ) {
        if(!networkMonitor.isOnline) {
            messageManager.postMessage(R.string.common_offline_message)
            return
        }
        loadingManager.show()
        viewModelScope.launch {
            val response = accountRepository.updateProfilePicture(uri.toString())
            loadingManager.hide()
            if (response is Response.Success) reload() else onFailed()
        }

    }

    private fun reload() {
        loadingManager.show()
        viewModelScope.launch {
            accountRepository.reloadUser()
            _user.update { accountRepository.currentUser }
            loadingManager.hide()
        }

    }

    override fun changePassword(
        oldPassword: String, newPassword: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        if(!networkMonitor.isOnline) {
            messageManager.postMessage(R.string.common_offline_message)
            return
        }
        loadingManager.show()
        viewModelScope.launch {
            val googleIdToken = userPreferencesRepository.googleIdToken.firstOrNull()
            val response = accountRepository.changePassword(
                oldPassword = oldPassword.ifEmpty { null },
                newPassword = newPassword,
                googleIdToken = googleIdToken
            )
            loadingManager.hide()
            if (response is Response.Success) onSuccess() else onFailed()


        }
    }


    override fun deleteUser(
        password: String, onSuccess: () -> Unit, onFailed: () -> Unit
    ) {
        loadingManager.show()
        viewModelScope.launch {
            val googleIdToken = userPreferencesRepository.googleIdToken.firstOrNull()
            val response = user.value?.let {
                trainingRepository.disableAll(it.uid)
                bmiInfoRepository.disableAll(it.uid)
                userRepository.delete(it.uid)
                accountRepository.deleteAccount(password.ifEmpty { null }, googleIdToken)
            } ?: Response.Error(message = "Invalid User")
            loadingManager.hide()
            if (response is Response.Success) onSuccess() else onFailed()
        }
    }
}