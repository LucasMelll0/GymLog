package com.example.gymlog.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymlog.model.Training
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    @Query("SELECT * FROM Training WHERE userId = :userId AND isDisabled = 0")
    fun getAllFlow(userId: String): Flow<List<Training>>

    @Query("SELECT * FROM Training WHERE userId = :userId AND isDisabled = 0")
    suspend fun getAll(userId: String): List<Training>

    @Query("SELECT * FROM Training WHERE trainingId = :id AND userId = :userId")
    suspend fun getById(id: String, userId: String): Training?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(training: Training)

    @Delete
    suspend fun delete(training: Training)

    @Query("SELECT * FROM Training WHERE userId = :userId AND isSynchronized = 0")
    suspend fun getAllUnSynchronized(userId: String): List<Training>

    @Query("SELECT * FROM Training WHERE userId = :userId AND isDisabled = 1")
    suspend fun getAllDisabled(userId: String): List<Training>
}