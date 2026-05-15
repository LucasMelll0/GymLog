package com.devmello.gymlog.repository

import com.devmello.gymlog.core.model.Response
import com.devmello.gymlog.core.model.repositories.UserRepository
import com.devmello.gymlog.data.cloud_db.CloudDB
import com.devmello.gymlog.data.dao.UserDao
import com.devmello.gymlog.model.User
import kotlinx.coroutines.flow.Flow
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
                if (response is Response.Success) dao.delete(userId)
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