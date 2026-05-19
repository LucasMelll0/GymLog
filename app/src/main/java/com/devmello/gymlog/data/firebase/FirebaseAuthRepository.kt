package com.devmello.gymlog.data.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.devmello.gymlog.R
import com.devmello.gymlog.core.model.repositories.AuthRepository
import com.devmello.gymlog.core.model.AuthResult
import com.devmello.gymlog.core.model.Response
import com.devmello.gymlog.core.model.UserCredentials
import com.devmello.gymlog.core.model.UserData
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class FirebaseAuthRepository(private val context: Context) : AuthRepository {

    private val credentialManager = CredentialManager.create(context)

    private val auth = Firebase.auth

    override suspend fun registerWithEmailAndPassword(userCredentials: UserCredentials): AuthResult {
        return try {
            val userData =
                auth.createUserWithEmailAndPassword(userCredentials.email, userCredentials.password)
                    .await().user
            val profileUpdates = userProfileChangeRequest {
                displayName = userCredentials.userName
            }
            userData?.updateProfile(profileUpdates)?.await()
            val signedUser = userData?.run {
                UserData(
                    uid = uid,
                    userName = displayName,
                    profilePicture = photoUrl?.toString()
                )
            } ?: currentUserData
            AuthResult.Success(
                data = signedUser ?: throw Exception("No user data")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Error(
                errorMessage = e.message ?: "Ocorreu um erro ao "
            )
        }
    }

    override suspend fun signInWithEmailAndPassword(userCredentials: UserCredentials): AuthResult {
        return try {
            val userData =
                auth.signInWithEmailAndPassword(userCredentials.email, userCredentials.password)
                    .await().user
            AuthResult.Success(
                data = userData?.run {
                    UserData(
                        uid = uid,
                        userName = displayName,
                        profilePicture = photoUrl?.toString()
                    )
                } ?: throw Exception("No user data")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Error(
                errorMessage = e.message
                    ?: context.getString(R.string.common_login_error_message)
            )
        }
    }

    override suspend fun signInWithGoogle(alreadyRegistered: Boolean): AuthResult {
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
            val userData = currentUserData ?: throw Exception("No user data")
            AuthResult.Success(
                data = userData.copy(googleIdToken = idToken)
            )
        } catch (e: GetCredentialException) {
            AuthResult.Error(
                errorMessage = e.message ?: context.getString(R.string.get_credential_error_message)
            )
        } catch (e: NoCredentialException) {
            AuthResult.Error(
                errorMessage = e.message ?: context.getString(R.string.get_credential_error_message)
            )
        }catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            AuthResult.Error(
                errorMessage = e.message ?: context.getString(R.string.common_login_error_message)
            )
        }
    }

    private suspend fun handleSignIn(
        credential: Credential,
        onSuccess: (idToken: String?) -> Unit
    ) {
        when (credential) {
            is CustomCredential if credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
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

    override val currentUserData: UserData?
        get() = auth.currentUser?.run {
        UserData(
            uid = uid,
            userName = displayName,
            profilePicture = photoUrl?.toString()
        )
    }

    override suspend fun signOutUser() {
        auth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    override suspend fun sendPasswordResetEmail(email: String): Response<Nothing> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Response.Success(data = null)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(message = e.message)
        }
    }
}