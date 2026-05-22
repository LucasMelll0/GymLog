package com.devmello.gymlog.data.firebase

import android.net.Uri
import com.devmello.gymlog.utils.State
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class StorageClient {

    private val storageRef = Firebase.storage.reference
    private val userPhoto = storageRef.child(USERS_PHOTOS)

    suspend fun savePhoto(photo: Uri, userId: String): State<Uri> {
        return try {
            val photoRef = userPhoto.child("/$userId")
            val uploadTask = photoRef.putFile(photo).await().task.await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            State.Success(downloadUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            State.Error(e.message ?: "Unknown Error")
        }
    }

    suspend fun deletePhoto(userId: String) {
        try {
          val photoRef = userPhoto.child("/$userId")
          photoRef.delete().await()
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
}