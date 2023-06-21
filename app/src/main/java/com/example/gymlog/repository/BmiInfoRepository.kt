package com.example.gymlog.repository

import com.example.gymlog.data.dao.BmiInfoDao
import com.example.gymlog.data.firebase.FireStoreClient
import com.example.gymlog.model.BmiInfo
import kotlinx.coroutines.flow.Flow

interface BmiInfoRepository {

    fun getAll(): Flow<List<BmiInfo>>

    suspend fun save(bmiInfo: BmiInfo)

    suspend fun delete(bmiInfo: BmiInfo)

    suspend fun sync()

}

class BmiInfoRepositoryImpl(private val dao: BmiInfoDao, private val fireStore: FireStoreClient) :
    BmiInfoRepository {
    override fun getAll(): Flow<List<BmiInfo>> = dao.getAll()

    override suspend fun save(bmiInfo: BmiInfo) {
        if (bmiInfo.userId.isNotEmpty()) {
            dao.save(bmiInfo)
            fireStore.saveBmiInfo(bmiInfo)
        }
    }

    override suspend fun delete(bmiInfo: BmiInfo) {
        if (bmiInfo.userId.isNotEmpty()) {
            dao.delete(bmiInfo)
            fireStore.deleteBmiInfo(bmiInfo)
        }
    }

    override suspend fun sync() {
        TODO("Not yet implemented")
    }
}