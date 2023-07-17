package com.example.gymlog.ui.auth.authclient

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val uid: String,
    val userName: String?,
    val profilePicture: String?,
    val googleIdToken: String? = null
)
