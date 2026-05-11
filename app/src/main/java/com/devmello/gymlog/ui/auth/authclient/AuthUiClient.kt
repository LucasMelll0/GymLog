package com.devmello.gymlog.ui.auth.authclient

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.devmello.gymlog.R
import com.devmello.gymlog.utils.Response
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
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

    suspend fun signInWithGoogle(alreadyRegistered: Boolean = true): SignInResult {
        return try {
            val googleIdOption =
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(alreadyRegistered)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            var idToken: String? = null
            handleSignIn(result.credential) {
                idToken = it
                if (idToken == null) throw Exception("No id token")
            }
            val userData = getSignedInUser()
            if (userData == null) throw Exception("No user data")
            SignInResult(
                data = userData.copy(googleIdToken = idToken),
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

    private suspend fun handleSignIn(
        credential: Credential,
        onSuccess: (idToken: String?) -> Unit
    ) {
        when (credential) {
            is CustomCredential  if credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                firebaseAuthWithGoogle(googleIdTokenCredential.idToken) { idToken ->
                    onSuccess(idToken)
                }
            }
        }
    }

    private suspend fun firebaseAuthWithGoogle(
        idToken: String,
        onSuccess: (idToken: String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess(idToken)
            } else {
                Log.w("firebaseAuthWithGoogle", "signInWithCredential:failure", task.exception)
            }
        }.await()
    }


    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            uid = uid,
            userName = displayName,
            profilePicture = photoUrl?.toString()
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
