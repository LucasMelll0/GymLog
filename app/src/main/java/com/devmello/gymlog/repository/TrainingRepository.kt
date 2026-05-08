package com.devmello.gymlog.repository

import android.util.Log
import com.devmello.gymlog.data.cloud_db.CloudDB
import com.devmello.gymlog.data.dao.TrainingDao
import com.devmello.gymlog.model.Training
import kotlinx.coroutines.flow.Flow


interface TrainingRepository {

    fun getAll(userId: String): Flow<List<Training>>

    suspend fun getById(id: String, userId: String): Training?

    suspend fun save(training: Training)

    suspend fun disable(training: Training)

    suspend fun disableAll(userId: String)

    suspend fun sync(userId: String)
}

class TrainingRepositoryImpl(private val dao: TrainingDao, private val cloudDbClient: CloudDB) :
    TrainingRepository {

    override fun getAll(userId: String): Flow<List<Training>> = dao.getAllFlow(userId)

    override suspend fun getById(id: String, userId: String): Training? = dao.getById(id, userId)

    override suspend fun save(training: Training) {
        dao.save(training.copy(isSynchronized = cloudDbClient.saveTraining(training).isSuccess))
    }

    override suspend fun disable(training: Training) {
        if (training.userId.isNotEmpty()) {
            dao.save(training.copy(isDisabled = true))
        }
    }

    override suspend fun disableAll(userId: String) {
        if (userId.isNotEmpty()) dao.disableAll(userId)
    }

    override suspend fun sync(userId: String) {
        val allDisabled = dao.getAllDisabled(userId)
        val allUnSynchronized = dao.getAllUnSynchronized(userId)
        val allLocal = dao.getAll(userId)
        val allCloud = cloudDbClient.getAllTrainings(userId)

        allDisabled.forEach {
            if (cloudDbClient.deleteTraining(it).isSuccess) {
                dao.delete(it)
            }
        }
        allUnSynchronized.forEach {
            if (cloudDbClient.saveTraining(it).isSuccess) {
                dao.save(it.copy(isSynchronized = true))
            } else {
                return@forEach
            }
        }
        if (allLocal.isEmpty()) {
            Log.i("TrainingRepository", "sync: $allCloud")
            allCloud?.forEach {
                allDisabled.find { disabled -> disabled.trainingId == it.trainingId } ?: run {
                    dao.save(it)
                }
            }
        }

    }

}