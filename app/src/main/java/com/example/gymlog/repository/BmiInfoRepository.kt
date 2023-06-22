package com.example.gymlog.repository

import com.example.gymlog.data.dao.BmiInfoDao
import com.example.gymlog.data.firebase.FireStoreClient
import com.example.gymlog.model.BmiInfo
import kotlinx.coroutines.flow.Flow

interface BmiInfoRepository {

    fun getAll(userId: String): Flow<List<BmiInfo>>

    suspend fun save(bmiInfo: BmiInfo)

    suspend fun disable(bmiInfo: BmiInfo)

    suspend fun sync(userId: String)

}

class BmiInfoRepositoryImpl(private val dao: BmiInfoDao, private val fireStore: FireStoreClient) :
    BmiInfoRepository {
    override fun getAll(userId: String): Flow<List<BmiInfo>> = dao.getAllFlow(userId)

    override suspend fun save(bmiInfo: BmiInfo) {
        if (bmiInfo.userId.isNotEmpty()) {
            dao.save(bmiInfo)
            fireStore.saveBmiInfo(bmiInfo)
        }
    }

    override suspend fun disable(bmiInfo: BmiInfo) {
        if (bmiInfo.userId.isNotEmpty()) {
            dao.save(bmiInfo.copy(isDisabled = true))
        }
    }

    override suspend fun sync(userId: String) {
        val allDisabled = dao.getAllDisabled(userId)
        val allLocal = dao.getAll(userId)
        val allCloud = fireStore.getHistoric(userId)
        allDisabled.forEach {
            if (fireStore.deleteBmiInfo(it).isSuccess) {
                dao.delete(it)
            }
        }
        if (allLocal.isNotEmpty()) {
            if (allLocal != allCloud) {
                allLocal.forEach {
                    fireStore.saveBmiInfo(it)
                }
            }
        } else {
            if (allCloud?.isNotEmpty() == true) {
                allCloud.forEach {
                    dao.save(it)
                }
            }
        }
    }
}