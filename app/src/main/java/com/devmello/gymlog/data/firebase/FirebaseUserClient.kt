package com.devmello.gymlog.data.firebase

import com.devmello.gymlog.core.model.Response
import com.devmello.gymlog.core.model.repositories.AccountRepository
import com.devmello.gymlog.extensions.capitalizeAllWords
import com.devmello.gymlog.extensions.toUserData
import com.devmello.gymlog.utils.Resource
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import androidx.core.net.toUri

class FirebaseUserClient(
    private val storageClient: StorageClient
) : AccountRepository {

    private val firebaseAuth = Firebase.auth
    private val user = firebaseAuth.currentUser
    override val currentUser = user?.toUserData()
    override val userProvider = user.let { user ->
        val providerData = user?.providerData
        providerData?.let { providerData ->
            if (providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }) {
                GoogleAuthProvider.PROVIDER_ID
            } else if (providerData.any { it.providerId == EmailAuthProvider.PROVIDER_ID }) {
                EmailAuthProvider.PROVIDER_ID
            } else {
                throw Exception("Invalid auth provider")
            }
        }
    }


    private suspend fun reAuthenticate(password: String? = null, googleIdToken: String? = null) {
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

    override suspend fun changePassword(
        oldPassword: String?,
        newPassword: String,
        googleIdToken: String?
    ): Response<Nothing> {
        return user?.let {
            try {
                reAuthenticate(oldPassword, googleIdToken)
                it.updatePassword(newPassword.trim()).await()
                Response.Success(data = null)
            } catch (e: Exception) {
                e.printStackTrace()
                Response.Error(message = e.message)
            }
        } ?: Response.Error(message = "Invalid user!")
    }

    override suspend fun deleteAccount(
        password: String?,
        googleIdToken: String?
    ): Response<Nothing> {
        return user?.let {
            try {
                reAuthenticate(password, googleIdToken)
                storageClient.deletePhoto(it.uid)
                it.delete().await()
                reloadUser()
                Response.Success(data = null)
            } catch (e: Exception) {
                e.printStackTrace()
                Response.Error(message = e.message)
            }
        } ?: Response.Error(message = "Invalid user!")
    }

    override suspend fun updateUsername(newName: String): Response<Nothing> {
        return user?.let {
            val profileUpdate = userProfileChangeRequest {
                displayName = newName.capitalizeAllWords()
            }
            try {
                withTimeout(5000) {
                    it.updateProfile(profileUpdate).await()
                }
                Response.Success(data = null)
            } catch (e: Exception) {
                e.printStackTrace()
                Response.Error(message = e.message)
            }
        } ?: Response.Error(message = "Invalid user!")
    }

    override suspend fun updateProfilePicture(photo: String): Response<Nothing> {
        return user?.let { user ->
            try {
                val uri = photo.toUri()
                return when (val resource = storageClient.savePhoto(uri, user.uid)) {
                    is Resource.Success -> {
                        val downloadUri = resource.data
                        val profileUpdate = userProfileChangeRequest {
                            photoUri = downloadUri
                        }
                        user.updateProfile(profileUpdate).await()
                        Response.Success(data = null)
                    }

                    else -> throw Exception("Error on upload image")
                }


            } catch (e: Exception) {
                e.printStackTrace()
                Response.Error(message = e.message)
            }
        } ?: Response.Error(message = "Invalid user!")
    }

    override suspend fun reloadUser() {
        try {
            withTimeout(5000) {
                user?.reload()?.await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}