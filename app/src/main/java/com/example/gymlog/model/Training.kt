package com.example.gymlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Training(
    @PrimaryKey
    val trainingId: String = UUID.randomUUID().toString(),
    val title: String = "",
    val filters: List<String> = emptyList(),
    val exercises: List<Exercise> = emptyList()
)