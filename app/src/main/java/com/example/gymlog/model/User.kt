package com.example.gymlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gymlog.utils.Gender
import java.util.UUID

@Entity
data class User(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val gender: Gender? = null,
    val height: Int = 0,
    val age: Int = 0
) {
}