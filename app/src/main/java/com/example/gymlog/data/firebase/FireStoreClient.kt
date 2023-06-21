package com.example.gymlog.data.firebase

import com.example.gymlog.model.BmiInfo
import com.example.gymlog.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FireStoreClient {

    private val db = Firebase.firestore


    suspend fun saveUserInfo(user: User) {
        try {
            db.collection(USERS)
                .document(user.id)
                .set(user)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getUser(id: String): User? {
        return try {
            db.collection(USERS).document(id).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveBmiInfo(bmiInfo: BmiInfo) {
        try {
            db.collection(BMI_INFO)
                .document(bmiInfo.userId)
                .collection(HISTORIC)
                .document(bmiInfo.id)
                .set(bmiInfo)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteBmiInfo(bmiInfo: BmiInfo) {
        try {
            db.collection(BMI_INFO)
                .document(bmiInfo.userId)
                .collection(HISTORIC)
                .document(bmiInfo.id)
                .delete()
                .await()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getHistoric(userId: String): List<BmiInfo>? {
        return try {
            db.collection(BMI_INFO)
                .document(userId)
                .collection(HISTORIC)
                .get()
                .await()
                .toObjects(BmiInfo::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}