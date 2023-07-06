package com.example.gymlog.data.firebase

import com.example.gymlog.extensions.capitalizeAllWords
import com.example.gymlog.utils.Response
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class FirebaseUserClient {

    private val firebaseAuth = Firebase.auth
    val user = firebaseAuth.currentUser

    suspend fun changeUsername(username: String): Response {
        return user?.let {
            val profileUpdate = userProfileChangeRequest {
                displayName = username.capitalizeAllWords()
            }
            try {
                withTimeout(5000) {
                    it.updateProfile(profileUpdate).await()
                }
                Response(isSuccess = true)
            } catch (e: Exception) {
                e.printStackTrace()
                Response(isSuccess = false, errorMessage = e.message)
            }
        } ?: Response(isSuccess = false, errorMessage = "Invalid User")
    }

    suspend fun reload() {
        try {
            withTimeout(5000) {
                user?.reload()?.await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}