package com.example.gymlog.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymlog.model.BmiInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface BmiInfoDao {

    @Query("SELECT * FROM BmiInfo WHERE userId = :userId AND isDisabled = 0")
    fun getAllFlow(userId: String): Flow<List<BmiInfo>>

    @Query("SELECT * FROM BmiInfo WHERE userId = :userId AND isDisabled = 0")
    suspend fun getAll(userId: String): List<BmiInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(bmiInfo: BmiInfo)

    @Delete
    suspend fun delete(bmiInfo: BmiInfo)

    @Query("SELECT * FROM BmiInfo WHERE userId = :userId AND isDisabled = 1")
    suspend fun getAllDisabled(userId: String): List<BmiInfo>

}