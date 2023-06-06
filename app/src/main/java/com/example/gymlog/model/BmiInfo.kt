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
    val gender: Gender,
    val weight: Float,
    val height: Int,
    val age: Int,
    val dateInMillis: Long = Date().time
)