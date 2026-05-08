package com.devmello.gymlog.ui.auth.authclient

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.credentials.ClearCredentialStateRequest
import com.devmello.gymlog.R
import com.devmello.gymlog.utils.Response
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class AuthUiClient(
    private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)
    private val auth = Firebase.auth

    suspend fun registerWithEmailAndPassword(
        userCredentials: UserCredentials
    ): SignInResult {
        return try {
            val userData =
                auth.createUserWithEmailAndPassword(userCredentials.email, userCredentials.password)
                    .await().user
            val profileUpdates = userProfileChangeRequest {
                displayName = userCredentials.userName
            }
            userData?.updateProfile(profileUpdates)?.await()
            SignInResult(
                data = userData?.run {
                    UserData(
                        uid = uid,
                        userName = displayName,
                        profilePicture = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }

    }

    suspend fun signInWithEmailAndPassword(userCredentials: UserCredentials): SignInResult {
        return try {
            val userData =
                auth.signInWithEmailAndPassword(userCredentials.email, userCredentials.password)
                    .await().user
            SignInResult(
                data = userData?.run {
                    UserData(
                        uid = uid,
                        userName = displayName,
                        profilePicture = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signInWithGoogle(): SignInResult {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .setAutoSelectEnabled(true)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = credentialManager.getCredential(context, request)

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val googleIdToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val userData = auth.signInWithCredential(firebaseCredential).await().user

            SignInResult(
                data = userData?.run {
                    UserData(
                        uid = uid,
                        userName = displayName,
                        profilePicture = photoUrl?.toString(),
                        googleIdToken = googleIdToken
                    )
                },
                errorMessage = null
            )
        } catch (e: GetCredentialException) {
            SignInResult(data = null, errorMessage = e.message)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            SignInResult(data = null, errorMessage = e.message)
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            uid = uid,
            userName = displayName,
            profilePicture = photoUrl?.toString(),
        )
    }

    suspend fun signOutUser() {
        auth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    suspend fun sendPasswordResetEmail(email: String): Response {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Response(isSuccess = true)
        } catch (e: Exception) {
            e.printStackTrace()
            Response(isSuccess = false, errorMessage = e.message)
        }
    }
}
