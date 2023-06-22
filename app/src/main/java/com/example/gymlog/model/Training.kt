package com.example.gymlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import java.util.UUID

@Entity
data class Training(
    @PrimaryKey
    val trainingId: String = UUID.randomUUID().toString(),
    val title: String = "",
    val isDisabled: Boolean = false,
    val userId: String = "",
    val isSynchronized: Boolean = false,
    val filters: List<String> = emptyList(),
    val exercises: List<Exercise> = emptyList()
) {
    @Exclude
    fun getExercisesWithMutableState(): List<ExerciseMutableState> = exercises.map {
        ExerciseMutableState(
            id = it.exerciseId,
            title = it.title,
            repetitions = it.repetitions,
            series = it.series,
            initialChecked = it.isChecked,
            observations = it.observations,
            filters = it.filters
        )
    }

}