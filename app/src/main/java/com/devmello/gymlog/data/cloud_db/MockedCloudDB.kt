package com.devmello.gymlog.data.cloud_db

import com.devmello.gymlog.model.BmiInfo
import com.devmello.gymlog.model.Training
import com.devmello.gymlog.model.User
import com.devmello.gymlog.utils.Response

class MockedCloudDB : CloudDB {
    override suspend fun saveUserInfo(user: User) {
        return
    }

    override suspend fun getUser(id: String): User? {
        return null
    }

    override suspend fun saveBmiInfo(bmiInfo: BmiInfo) {
        return
    }

    override suspend fun deleteBmiInfo(bmiInfo: BmiInfo): Response {
        return Response(isSuccess = true)
    }

    override suspend fun getHistoric(userId: String): List<BmiInfo>? {
        return null
    }

    override suspend fun saveTraining(training: Training): Response {
        return Response(isSuccess = true)
    }

    override suspend fun deleteTraining(training: Training): Response {
        return Response(isSuccess = true)
    }

    override suspend fun getAllTrainings(userId: String): List<Training>? {
        return null
    }

    override suspend fun deleteAllUserData(userId: String): Response {
        return Response(isSuccess = true)
    }
}