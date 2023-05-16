package com.example.gymlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Training(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val exercises: List<ExerciseMutableState> = emptyList(),
    val filters: List<String> = emptyList()
)