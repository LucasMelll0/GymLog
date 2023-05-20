package com.example.gymlog.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID

class ExerciseMutableState(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val repetitions: Int,
    val series: Int,
    initialChecked: Boolean = false,

    ) {
    var isChecked: Boolean by mutableStateOf(initialChecked)

    fun toExercise() = Exercise(
        exerciseId = this.id,
        title = this.title,
        repetitions = this.repetitions,
        series = this.series,
        isChecked = this.isChecked
        )

    override fun toString(): String {
        return "nome: $title \n $repetitions X $series"
    }
}