package com.example.gymlog.repository

import com.example.gymlog.database.dao.UserDao
import com.example.gymlog.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser(): Flow<User?>

    suspend fun saveUser(user: User)

}

class UserRepositoryImpl(private val dao: UserDao) : UserRepository {

    override fun getUser(): Flow<User?> = dao.getUser()

    override suspend fun saveUser(user: User) = dao.saveUser(user)

}