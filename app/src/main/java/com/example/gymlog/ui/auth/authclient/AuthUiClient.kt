package com.example.gymlog.ui.auth.authclient

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.gymlog.R
import com.example.gymlog.ui.auth.data.SignInResult
import com.example.gymlog.ui.auth.data.UserCredentials
import com.example.gymlog.ui.auth.data.UserData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class AuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {

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
                        userId = uid,
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
                        userId = uid,
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

    suspend fun signInWithGoogle(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(buildSignInRequest()).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val userData = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = userData?.run {
                    UserData(
                        userId = uid,
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

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            userName = displayName,
            profilePicture = photoUrl?.toString()
        )
    }

    fun signOutUser() {
        auth.signOut()
    }

}