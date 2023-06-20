package com.example.gymlog.ui.auth.authclient

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
