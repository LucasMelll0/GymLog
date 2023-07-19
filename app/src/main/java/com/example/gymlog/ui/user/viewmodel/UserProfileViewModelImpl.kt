package com.example.gymlog.ui.user.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.gymlog.data.firebase.FirebaseUserClient
import com.example.gymlog.extensions.toUserData
import com.example.gymlog.repository.BmiInfoRepository
import com.example.gymlog.repository.TrainingRepository
import com.example.gymlog.repository.UserRepository
import com.example.gymlog.ui.auth.authclient.UserData
import com.example.gymlog.utils.Response
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
    ): Response

    suspend fun deleteUser(
        password: String,
        googleIdToken: String? = null,
    ): Response

}

class UserProfileViewModelImpl(
    private val userClient: FirebaseUserClient,
    private val trainingRepository: TrainingRepository,
    private val bmiInfoRepository: BmiInfoRepository,
    private val userRepository: UserRepository
) : UserProfileViewModel, ViewModel() {

    private val _user = MutableStateFlow(userClient.user?.toUserData())
    override val user: StateFlow<UserData?> get() = _user

    override val userProvider = userClient.userProvider

    override suspend fun changeUsername(
        username: String,
        onFailedListener: suspend () -> Unit
    ) {
        val response = userClient.changeUsername(username)
        if (response.isSuccess) reload() else onFailedListener()
    }

    override suspend fun changeUserPhoto(
        uri: Uri,
        onFailedListener: suspend () -> Unit
    ) {
        val response = userClient.changeUserPhoto(uri)
        if (response.isSuccess) reload() else onFailedListener()
    }

    private suspend fun reload() {
        userClient.reload()
        _user.update { userClient.user?.toUserData() }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        googleIdToken: String?,
    ) =
        userClient.changePassword(
            oldPassword = oldPassword.ifEmpty { null },
            newPassword = newPassword,
            googleIdToken = googleIdToken
        )

    override suspend fun deleteUser(
        password: String,
        googleIdToken: String?
    ): Response {
        return user.value?.let {
            trainingRepository.disableAll(it.uid)
            bmiInfoRepository.disableAll(it.uid)
            userRepository.delete(it.uid)
            userClient.deleteUser(password.ifEmpty { null }, googleIdToken)
        } ?: Response(isSuccess = false, errorMessage = "Invalid User")
    }
}