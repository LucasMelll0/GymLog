package com.example.gymlog.repository

import com.example.gymlog.data.dao.UserDao
import com.example.gymlog.data.firebase.FireStoreClient
import com.example.gymlog.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser(): Flow<User?>


    suspend fun saveUser(user: User)

    suspend fun sync(id: String)

}

class UserRepositoryImpl(
    private val dao: UserDao,
    private val fireStore: FireStoreClient
) : UserRepository {

    override fun getUser(): Flow<User?> = dao.getUser()

    override suspend fun saveUser(user: User) {
        dao.saveUser(user)
        fireStore.saveUserInfo(user)
    }

    override suspend fun sync(id: String) {
        val localUser = dao.getUserById(id)
        val cloudUser = fireStore.getUser(id)
        localUser?.let {
            if (localUser != cloudUser) {
                fireStore.saveUserInfo(localUser)
            }
        } ?: run {
            cloudUser?.let {
                dao.saveUser(cloudUser)
            }
        }
    }

}