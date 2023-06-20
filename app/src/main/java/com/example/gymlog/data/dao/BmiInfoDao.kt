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

    @Query("SELECT * FROM BmiInfo")
    fun getAll(): Flow<List<BmiInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(bmiInfo: BmiInfo)

    @Delete
    suspend fun delete(bmiInfo: BmiInfo)

}