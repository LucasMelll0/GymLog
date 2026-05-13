package com.devmello.gymlog.data.cloud_db

import com.devmello.gymlog.model.BmiInfo
import com.devmello.gymlog.model.Training
import com.devmello.gymlog.model.User
import com.devmello.gymlog.utils.Response

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