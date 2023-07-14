package com.example.gymlog.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymlog.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM User WHERE id = :userId")
    fun getUser(userId: String): Flow<User?>

    @Query("DELETE FROM User WHERE id = :userId")
    fun delete(userId: String)

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun getUserById(id: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: User)

}