package com.devmello.gymlog.extensions

import com.devmello.gymlog.core.model.UserData
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUserData() = UserData(
    uid = this.uid,
    userName = this.displayName,
    profilePicture = this.photoUrl.toString()
)
