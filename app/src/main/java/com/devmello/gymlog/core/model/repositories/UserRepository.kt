package com.devmello.gymlog.core.model.repositories

import com.devmello.gymlog.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser(userId: String): Flow<User?>


    suspend fun saveUser(user: User)

    suspend fun delete(userId: String)

    suspend fun sync(id: String)
}