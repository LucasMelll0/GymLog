package com.example.gymlog.data.firebase

import android.net.Uri
import com.example.gymlog.extensions.capitalizeAllWords
import com.example.gymlog.utils.Resource
import com.example.gymlog.utils.Response
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class FirebaseUserClient(
    private val storageClient: StorageClient
) {

    private val firebaseAuth = Firebase.auth
    val user = firebaseAuth.currentUser
    val userProvider = user.let { user ->
        val providerData = user?.providerData
        providerData?.let {
            if (providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }) {
                GoogleAuthProvider.PROVIDER_ID
            } else if (providerData.any { it.providerId == EmailAuthProvider.PROVIDER_ID }) {
                EmailAuthProvider.PROVIDER_ID
            } else {
                throw Exception("Invalid auth provider")
            }
        }
    }


    private suspend fun reauthenticate(password: String? = null, googleIdToken: String? = null) {
        user?.let { user ->
            try {
                when (userProvider) {
                    GoogleAuthProvider.PROVIDER_ID -> {
                        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
                        user.reauthenticate(credential).await()
                    }

                    EmailAuthProvider.PROVIDER_ID -> {
                        val credential = EmailAuthProvider.getCredential(user.email!!, password!!)
                        user.reauthenticate(credential).await()
                    }

                    else -> throw Exception("Invalid provider")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun changePassword(
        oldPassword: String?,
        newPassword: String,
        googleIdToken: String? = null
    ): Response {
        return user?.let {
            try {
                reauthenticate(oldPassword, googleIdToken)
                it.updatePassword(newPassword.trim()).await()
                Response(isSuccess = true)
            } catch (e: Exception) {
                e.printStackTrace()
                Response(isSuccess = false, errorMessage = e.message)
            }
        } ?: Response(isSuccess = false, errorMessage = "Invalid user!")
    }

    suspend fun deleteUser(
        password: String?,
        googleIdToken: String?
    ) : Response {
        return user?.let {
            try {
                reauthenticate(password, googleIdToken)
                it.delete().await()
                Response(isSuccess = true)
            }catch (e: Exception) {
                e.printStackTrace()
                Response(isSuccess = false, errorMessage = e.message)
            }
        } ?: Response(isSuccess = false, errorMessage = "Invalid user!")
    }

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

    suspend fun changeUserPhoto(photo: Uri): Response {
        return user?.let {
            try {
                return when(val resource = storageClient.savePhoto(photo, it.uid)) {
                    is Resource.Success -> {
                        val downloadUri = resource.data
                        val profileUpdate = userProfileChangeRequest {
                            photoUri = downloadUri
                        }
                        it.updateProfile(profileUpdate).await()
                        Response(isSuccess = true)
                    }
                    else -> throw Exception("Error on upload image")
                }


            }catch (e: Exception) {
                e.printStackTrace()
                Response(isSuccess = false, errorMessage = e.message ?: "Unknown Error")
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