package com.devmello.gymlog.core.model

data class UserData(
    val uid: String,
    val userName: String?,
    val profilePicture: String?,
    val googleIdToken: String? = null
)
