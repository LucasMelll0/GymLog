package com.devmello.gymlog.data

import com.devmello.gymlog.model.Exercise
import kotlinx.serialization.Serializable

@Serializable
data class GeneratedTrainingDto(
    val exercises: List<ExerciseDto>
)

@Serializable
data class ExerciseDto(
    val title: String,
    val observations: String?,
    val series: Int,
    val repetitions: Int,
    val filters: List<String> = emptyList()
) {
    fun toDomain(): Exercise = Exercise(
        title = title,
        observations = observations ?: "",
        series = series,
        repetitions = repetitions,
        filters = filters
    )
}
