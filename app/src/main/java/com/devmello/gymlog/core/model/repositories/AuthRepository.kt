package com.devmello.gymlog.core.model.repositories

import com.devmello.gymlog.core.model.AuthResult
import com.devmello.gymlog.core.model.Response
import com.devmello.gymlog.core.model.UserCredentials
import com.devmello.gymlog.core.model.UserData

interface AuthRepository {
    suspend fun registerWithEmailAndPassword(userCredentials: UserCredentials): AuthResult
    suspend fun signInWithEmailAndPassword(userCredentials: UserCredentials): AuthResult
    suspend fun signInWithGoogle(alreadyRegistered: Boolean = true): AuthResult
    val currentUserData: UserData?
    suspend fun signOutUser()
    suspend fun sendPasswordResetEmail(email: String): Response<Nothing>
}