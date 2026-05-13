package com.devmello.gymlog.ui.auth.authclient

sealed class AuthResult {
    data class Success(val data: UserData) : AuthResult()
    data class Error(val errorMessage: String) : AuthResult()
}

data class UserData(
    val uid: String,
    val userName: String?,
    val profilePicture: String?,
    val googleIdToken: String? = null
)
