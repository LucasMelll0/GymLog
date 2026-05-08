package com.devmello.gymlog.repository

import com.devmello.gymlog.data.cloud_db.CloudDB
import com.devmello.gymlog.data.dao.BmiInfoDao
import com.devmello.gymlog.model.BmiInfo
import kotlinx.coroutines.flow.Flow

interface BmiInfoRepository {

    fun getAll(userId: String): Flow<List<BmiInfo>>

    suspend fun save(bmiInfo: BmiInfo)

    suspend fun disable(bmiInfo: BmiInfo)

    suspend fun disableAll(userId: String)

    suspend fun sync(userId: String)

}

class BmiInfoRepositoryImpl(private val dao: BmiInfoDao, private val cloudDB: CloudDB) :
    BmiInfoRepository {
    override fun getAll(userId: String): Flow<List<BmiInfo>> = dao.getAllFlow(userId)

    override suspend fun save(bmiInfo: BmiInfo) {
        if (bmiInfo.userId.isNotEmpty()) {
            dao.save(bmiInfo)
            cloudDB.saveBmiInfo(bmiInfo)
        }
    }

    override suspend fun disable(bmiInfo: BmiInfo) {
        if (bmiInfo.userId.isNotEmpty()) {
            dao.save(bmiInfo.copy(isDisabled = true))
        }
    }

    override suspend fun disableAll(userId: String) {
        if (userId.isNotEmpty()) dao.disableAll(userId)
    }

    override suspend fun sync(userId: String) {
        val allDisabled = dao.getAllDisabled(userId)
        val allLocal = dao.getAll(userId)
        val allCloud = cloudDB.getHistoric(userId)
        allDisabled.forEach {
            if (cloudDB.deleteBmiInfo(it).isSuccess) {
                dao.delete(it)
            }
        }
        if (allLocal.isNotEmpty()) {
            if (allLocal != allCloud) {
                allLocal.forEach {
                    cloudDB.saveBmiInfo(it)
                }
            }
        } else {
            allCloud?.forEach {
                allDisabled.find { disabled -> disabled.id == it.id } ?: dao.save(it)
            }
        }
    }
}