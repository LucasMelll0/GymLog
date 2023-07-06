package com.example.gymlog.ui.user.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymlog.data.firebase.FirebaseUserClient
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserProfileViewModel(private val userClient: FirebaseUserClient) : ViewModel() {

    private val _user = MutableStateFlow(userClient.user)
    internal val user: StateFlow<FirebaseUser?> get() = _user

    suspend fun changeUsername(username: String) = userClient.changeUsername(username)

    suspend fun reload() = userClient.reload()

}