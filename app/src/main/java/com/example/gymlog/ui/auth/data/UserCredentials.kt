package com.example.gymlog.ui.auth.data

data class UserCredentials(
    val email: String,
    val password: String,
    val userName: String? = null
)