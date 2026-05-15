package com.devmello.gymlog.data.cloud_db

import com.devmello.gymlog.core.model.Response
import com.devmello.gymlog.model.BmiInfo
import com.devmello.gymlog.model.Training
import com.devmello.gymlog.model.User

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

    override suspend fun deleteBmiInfo(bmiInfo: BmiInfo): Response<Nothing> {
        return Response.Success(null)
    }

    override suspend fun getHistoric(userId: String): List<BmiInfo>? {
        return null
    }

    override suspend fun saveTraining(training: Training): Response<Nothing> {
        return Response.Success(null)
    }

    override suspend fun deleteTraining(training: Training): Response<Nothing> {
        return Response.Success(null)
    }

    override suspend fun getAllTrainings(userId: String): List<Training>? {
        return null
    }

    override suspend fun deleteAllUserData(userId: String): Response<Nothing> {
        return Response.Success(null)
    }
}