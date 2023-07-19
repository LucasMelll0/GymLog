package com.example.gymlog.data.firebase

import android.net.Uri
import com.example.gymlog.utils.Resource
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class StorageClient {

    private val storageRef = Firebase.storage.reference
    private val userPhoto = storageRef.child(USERS_PHOTOS)

    suspend fun savePhoto(photo: Uri, userId: String): Resource<Uri> {
        return try {
            val photoRef = userPhoto.child("/$userId")
            val uploadTask = photoRef.putFile(photo).await().task.await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            Resource.Success(downloadUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Unknown Error")
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