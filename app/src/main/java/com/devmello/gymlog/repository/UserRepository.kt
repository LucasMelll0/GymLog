package com.devmello.gymlog.repository

import com.devmello.gymlog.data.cloud_db.CloudDB
import com.devmello.gymlog.data.dao.UserDao
import com.devmello.gymlog.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser(userId: String): Flow<User?>


    suspend fun saveUser(user: User)

    suspend fun delete(userId: String)

    suspend fun sync(id: String)

}

class UserRepositoryImpl(
    private val dao: UserDao,
    private val cloudDb: CloudDB
) : UserRepository {

    override fun getUser(userId: String): Flow<User?> = dao.getUser(userId)

    override suspend fun saveUser(user: User) {
        dao.saveUser(user)
        cloudDb.saveUserInfo(user)
    }

    override suspend fun delete(userId: String) {
        if (userId.isNotEmpty()) {
            try {
                val response = cloudDb.deleteAllUserData(userId)
                if (response.isSuccess) dao.delete(userId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun sync(id: String) {
        val localUser = dao.getUserById(id)
        val cloudUser = cloudDb.getUser(id)
        localUser?.let {
            if (localUser != cloudUser) {
                cloudDb.saveUserInfo(localUser)
            }
        } ?: run {
            cloudUser?.let {
                dao.saveUser(cloudUser)
            }
        }
    }

}