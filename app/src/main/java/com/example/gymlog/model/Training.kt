package com.example.gymlog.model

import java.util.UUID

class Training(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val exercises: List<Exercise> = emptyList(),
    val filters: List<String> = emptyList()
    ) {
}