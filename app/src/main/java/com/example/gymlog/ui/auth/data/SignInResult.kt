package com.example.gymlog.ui.auth.data

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val userName: String?,
    val profilePicture: String?
)
