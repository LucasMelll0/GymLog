package com.example.gymlog.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID

class Exercise(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val repetitions: Int,
    val series: Int,
    initialChecked: Boolean = false,

) {
    var isChecked: Boolean by mutableStateOf(initialChecked)
}