package com.example.gymlog.repository

import com.example.gymlog.database.dao.TrainingDao
import com.example.gymlog.model.Training
import kotlinx.coroutines.flow.Flow


interface TrainingRepository {

    fun getAll(): Flow<List<Training>>

    suspend fun getById(id: String): Flow<Training?>

    suspend fun save(training: Training)

    suspend fun remove(training: Training)
}

class TrainingRepositoryImpl(private val dao: TrainingDao) : TrainingRepository {

    override fun getAll(): Flow<List<Training>> = dao.getAll()

    override suspend fun getById(id: String): Flow<Training?> = dao.getByTrainingId(id)

    override suspend fun save(training: Training) = dao.saveTraining(training)

    override suspend fun remove(training: Training) = dao.delete(training)

}