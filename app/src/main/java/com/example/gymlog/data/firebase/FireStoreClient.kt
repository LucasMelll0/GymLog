package com.example.gymlog.data.firebase

import com.example.gymlog.model.BmiInfo
import com.example.gymlog.model.Training
import com.example.gymlog.model.User
import com.example.gymlog.utils.Response
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

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
            withTimeout(5000) {
                db.collection(USERS).document(id).get().await().toObject(User::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveBmiInfo(bmiInfo: BmiInfo) {
        try {
            withTimeout(5000) {
                db.collection(BMI_INFO)
                    .document(bmiInfo.userId)
                    .collection(HISTORIC)
                    .document(bmiInfo.id)
                    .set(bmiInfo)
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteBmiInfo(bmiInfo: BmiInfo): Response {
        return try {
            db.collection(BMI_INFO)
                .document(bmiInfo.userId)
                .collection(HISTORIC)
                .document(bmiInfo.id)
                .delete()
                .await()
            Response(isSuccess = true)
        } catch (e: Exception) {
            e.printStackTrace()
            Response(isSuccess = false)
        }
    }

    suspend fun getHistoric(userId: String): List<BmiInfo>? {
        return try {
            withTimeout(5000) {
                db.collection(BMI_INFO)
                    .document(userId)
                    .collection(HISTORIC)
                    .get()
                    .await()
                    .toObjects(BmiInfo::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveTraining(training: Training): Response {
        return try {
            withTimeout(5000) {
                db.collection(USERS)
                    .document(training.userId)
                    .collection(TRAININGS)
                    .document(training.trainingId)
                    .set(training.copy(isSynchronized = true))
                    .await()
                Response(isSuccess = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Response(isSuccess = false)
        }
    }

    suspend fun deleteTraining(training: Training): Response {
        return try {
            withTimeout(2000) {
                db.collection(USERS)
                    .document(training.userId)
                    .collection(TRAININGS)
                    .document(training.trainingId)
                    .delete()
                    .await()
                Response(isSuccess = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Response(isSuccess = false)
        }
    }

    suspend fun getAllTrainings(userId: String): List<Training>? {
        return try {
            withTimeout(2000) {
                db.collection(USERS)
                    .document(userId)
                    .collection(TRAININGS)
                    .get()
                    .await()
                    .toObjects(Training::class.java)
            }
        }catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteAllUserData(userId: String): Response {
        return try {
            withTimeout(10000) {
                db.collection(USERS)
                    .document(userId)
                    .collection(TRAININGS)
                    .get()
                    .await()
                    .toObjects(Training::class.java)
                    .forEach {
                        db.collection(USERS)
                            .document(userId)
                            .collection(TRAININGS)
                            .document(it.trainingId)
                            .delete()
                            .await()
                    }
                db.collection(USERS)
                    .document(userId)
                    .delete()
                    .await()
                db.collection(BMI_INFO)
                    .document(userId)
                    .collection(HISTORIC)
                    .get()
                    .await()
                    .toObjects(BmiInfo::class.java)
                    .forEach {
                        db.collection(BMI_INFO)
                            .document(userId)
                            .collection(HISTORIC)
                            .document(it.id)
                            .delete()
                            .await()
                    }
                db.collection(BMI_INFO)
                    .document(userId)
                    .delete()
                    .await()
                Response(isSuccess = true)
            }
        }catch (e: Exception) {
            e.printStackTrace()
            Response(isSuccess = false, errorMessage = e.message)
        }
    }

}