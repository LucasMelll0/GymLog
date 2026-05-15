package com.devmello.gymlog.ui.user.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.devmello.gymlog.core.model.Response
import com.devmello.gymlog.core.model.UserData
import com.devmello.gymlog.core.model.repositories.AccountRepository
import com.devmello.gymlog.core.model.repositories.UserRepository
import com.devmello.gymlog.extensions.toUserData
import com.devmello.gymlog.repository.BmiInfoRepository
import com.devmello.gymlog.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


interface UserProfileViewModel {
    val user: StateFlow<UserData?>
    val userProvider: String?

    suspend fun changeUsername(username: String, onFailedListener: suspend () -> Unit = {})

    suspend fun changeUserPhoto(uri: Uri, onFailedListener: suspend () -> Unit = {})

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        googleIdToken: String? = null,
    ): Response<Unit>

    suspend fun deleteUser(
        password: String,
        googleIdToken: String? = null,
    ): Response<Unit>

}

class UserProfileViewModelImpl(
    private val accountRepository: AccountRepository,
    private val trainingRepository: TrainingRepository,
    private val bmiInfoRepository: BmiInfoRepository,
    private val userRepository: UserRepository
) : UserProfileViewModel, ViewModel() {

    private val _user = MutableStateFlow(accountRepository.currentUser)
    override val user: StateFlow<UserData?> get() = _user

    override val userProvider = accountRepository.userProvider

    override suspend fun changeUsername(
        username: String,
        onFailedListener: suspend () -> Unit
    ) {
        val response = accountRepository.updateUsername(username)
        if (response is Response.Success) reload() else onFailedListener()
    }

    override suspend fun changeUserPhoto(
        uri: Uri,
        onFailedListener: suspend () -> Unit
    ) {
        val response = accountRepository.updateProfilePicture(uri.toString())
        if (response is Response.Success) reload() else onFailedListener()
    }

    private suspend fun reload() {
        accountRepository.reloadUser()
        _user.update { accountRepository.currentUser }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        googleIdToken: String?,
    ) =
        accountRepository.changePassword(
            oldPassword = oldPassword.ifEmpty { null },
            newPassword = newPassword,
            googleIdToken = googleIdToken
        )

    override suspend fun deleteUser(
        password: String,
        googleIdToken: String?
    ): Response<Unit> {
        return user.value?.let {
            trainingRepository.disableAll(it.uid)
            bmiInfoRepository.disableAll(it.uid)
            userRepository.delete(it.uid)
            accountRepository.deleteAccount(password.ifEmpty { null }, googleIdToken)
        } ?: Response.Error(message = "Invalid User")
    }
}