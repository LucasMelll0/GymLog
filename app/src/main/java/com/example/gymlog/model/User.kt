package com.example.gymlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gymlog.utils.Gender
import java.util.UUID

@Entity
class User(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val gender: Gender,
    val height: Int,
    val age: Int
) {
}