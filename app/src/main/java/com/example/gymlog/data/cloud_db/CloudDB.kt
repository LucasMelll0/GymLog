package com.example.gymlog.data.cloud_db

import com.example.gymlog.model.BmiInfo
import com.example.gymlog.model.Training
import com.example.gymlog.model.User
import com.example.gymlog.utils.Response

interface CloudDB {

    suspend fun saveUserInfo(user: User)

    suspend fun getUser(id: String): User?

    suspend fun saveBmiInfo(bmiInfo: BmiInfo)

    suspend fun deleteBmiInfo(bmiInfo: BmiInfo): Response

    suspend fun getHistoric(userId: String): List<BmiInfo>?

    suspend fun saveTraining(training: Training): Response

    suspend fun deleteTraining(training: Training): Response

    suspend fun getAllTrainings(userId: String): List<Training>?

    suspend fun deleteAllUserData(userId: String): Response


}