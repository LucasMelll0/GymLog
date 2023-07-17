package com.example.gymlog.extensions

import com.example.gymlog.ui.auth.authclient.UserData
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUserData() = UserData(
    uid = this.uid,
    userName = this.displayName,
    profilePicture = this.photoUrl.toString()
)
