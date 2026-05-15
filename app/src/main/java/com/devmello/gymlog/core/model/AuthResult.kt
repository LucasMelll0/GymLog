package com.devmello.gymlog.core.model

sealed class AuthResult {
    data class Success(val data: UserData) : AuthResult()
    data class Error(val errorMessage: String) : AuthResult()
}