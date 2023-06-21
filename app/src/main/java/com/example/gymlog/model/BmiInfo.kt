package com.example.gymlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gymlog.utils.Gender
import java.util.Date
import java.util.UUID

@Entity
data class BmiInfo(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val gender: Gender = Gender.Male,
    val weight: Float = 0f,
    val height: Int = 0,
    val age: Int = 0,
    val dateInMillis: Long = Date().time,
    val isDisabled: Boolean = false
)