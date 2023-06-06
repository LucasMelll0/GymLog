package com.example.gymlog.repository

import com.example.gymlog.database.dao.BmiInfoDao
import com.example.gymlog.model.BmiInfo
import kotlinx.coroutines.flow.Flow

interface BmiInfoRepository {

    fun getAll(): Flow<List<BmiInfo>>

    suspend fun save(bmiInfo: BmiInfo)

    suspend fun delete(bmiInfo: BmiInfo)

}

class BmiInfoRepositoryImpl(private val dao: BmiInfoDao) : BmiInfoRepository {
    override fun getAll(): Flow<List<BmiInfo>> = dao.getAll()

    override suspend fun save(bmiInfo: BmiInfo) = dao.save(bmiInfo)

    override suspend fun delete(bmiInfo: BmiInfo) = dao.delete(bmiInfo)
}