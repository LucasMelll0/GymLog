package com.example.gymlog.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.gymlog.model.Training
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    @Transaction
    @Query("SELECT * FROM Training")
    fun getAll(): Flow<List<Training>>

    @Transaction
    @Query("SELECT * FROM Training WHERE trainingId = :id")
    suspend fun getById(id: String): Training?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTraining(training: Training)
    
    @Transaction
    @Delete
    suspend fun delete(training: Training)
}