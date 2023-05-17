package com.example.gymlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Exercise(
    @PrimaryKey
    val exerciseId: String = UUID.randomUUID().toString(),
    val title: String,
    val repetitions: Int,
    val series: Int,
    val isChecked: Boolean = false,
)