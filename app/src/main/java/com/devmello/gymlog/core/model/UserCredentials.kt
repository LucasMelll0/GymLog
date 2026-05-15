package com.devmello.gymlog.core.model

data class UserCredentials(
    val email: String,
    val password: String,
    val userName: String? = null
)
