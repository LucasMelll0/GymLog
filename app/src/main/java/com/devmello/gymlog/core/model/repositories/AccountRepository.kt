package com.devmello.gymlog.core.model.repositories

import com.devmello.gymlog.core.model.Response
import com.devmello.gymlog.core.model.UserData

interface AccountRepository {

    val currentUser: UserData?
    val userProvider: String?

    suspend fun updateUsername(newName: String): Response<Unit>
    suspend fun changePassword(
        oldPassword: String?,
        newPassword: String,
        googleIdToken: String? = null
    ): Response<Unit>

    suspend fun deleteAccount(password: String? = null, googleIdToken: String? = null): Response<Unit>
    suspend fun reloadUser()
}