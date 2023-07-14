package com.example.gymlog.ui.user.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymlog.data.firebase.FirebaseUserClient
import com.example.gymlog.repository.BmiInfoRepository
import com.example.gymlog.repository.TrainingRepository
import com.example.gymlog.repository.UserRepository
import com.example.gymlog.utils.Response
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserProfileViewModel(
    private val userClient: FirebaseUserClient,
    private val trainingRepository: TrainingRepository,
    private val bmiInfoRepository: BmiInfoRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow(userClient.user)
    internal val user: StateFlow<FirebaseUser?> get() = _user

    internal val userProvider = userClient.userProvider

    suspend fun changeUsername(username: String, onFailedListener: suspend () -> Unit = {}) {
        val response = userClient.changeUsername(username)
        if (response.isSuccess) reload() else onFailedListener()
    }

    private suspend fun reload() = userClient.reload()

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        googleIdToken: String? = null,
    ) =
        userClient.changePassword(
            oldPassword = oldPassword.ifEmpty { null },
            newPassword = newPassword,
            googleIdToken = googleIdToken
        )

    suspend fun deleteUser(
        password: String,
        googleIdToken: String? = null,
    ) : Response {
        return user.value?.let {
            val response = userClient.deleteUser(password.ifEmpty { null }, googleIdToken)
            if (response.isSuccess) {
                trainingRepository.disableAll(it.uid)
                bmiInfoRepository.disableAll(it.uid)
                userRepository.delete(it.uid)
            }
            response
        } ?: Response(isSuccess = false, errorMessage = "Invalid User")
    }


}