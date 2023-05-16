package com.example.gymlog.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.gymlog.model.ExerciseMutableState
import com.example.gymlog.model.Training
import com.example.gymlog.utils.TrainingTypes
import kotlin.random.Random

class Mock {

    companion object {
        fun getFilters(): List<TrainingTypes> {
            val trainingTypes = TrainingTypes.values()
            val filters = mutableListOf<TrainingTypes>()
            val listSize = Random.nextInt(3, 5)
            while (filters.size < listSize) {
                trainingTypes[Random.nextInt(0, trainingTypes.lastIndex)].let { type ->
                    filters.find { it == type } ?: run {
                        filters.add(trainingTypes[Random.nextInt(0, trainingTypes.lastIndex)])
                    }
                }
            }
            return filters
        }

        fun getExercises(): List<ExerciseMutableState> {
            val exercisesStrings = listOf(
                "Corrida",
                "Flexões",
                "Agachamentos",
                "Abdominais",
                "Prancha",
                "Supino",
                "Burpees",
                "Pular corda",
                "Levantamento de peso",
                "Bicicleta ergométrica",
                "Yoga",
                "Zumba",
                "Pilates",
                "Natação",
                "Step",
                "Alongamento",
                "Musculação",
                "Caminhada",
                "Boxe",
                "Aeróbica"
            )
            val exercises = mutableListOf<ExerciseMutableState>()
            val listSize = Random.nextInt(4, 10)
            while (exercises.size < listSize) {
                val title = exercisesStrings.random()
                exercises.find { it.title == title } ?: run {
                    val exercise = ExerciseMutableState(
                        title = title,
                        repetitions = Random.nextInt(5, 20),
                        series = Random.nextInt(3, 8)
                    )
                    exercises.add(exercise)
                }
            }
            return exercises.toList()
        }
        @Composable
        fun getTrainings(size: Int = 20) = List(size) { i ->
            Training(
                title = "Training $i",
                exercises = getExercises(),
                filters = getFilters().map {
                    stringResource(id = it.stringRes())
                }
            )
        }
    }

}