package com.example.gymlog.ui.auth.authclient

data class UserCredentials(
    val email: String,
    val password: String,
    val userName: String? = null
)