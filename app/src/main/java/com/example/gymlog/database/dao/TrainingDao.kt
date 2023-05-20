package com.example.gymlog.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymlog.model.Training
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    @Query("SELECT * FROM Training")
    fun getAll(): Flow<List<Training>>

    @Query("SELECT * FROM Training WHERE trainingId = :id")
    fun getByTrainingId(id: String): Flow<Training>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTraining(training: Training)

    @Delete
    suspend fun delete(training: Training)
}