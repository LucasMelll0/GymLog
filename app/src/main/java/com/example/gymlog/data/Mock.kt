package com.example.gymlog.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.gymlog.model.BmiInfo
import com.example.gymlog.model.Exercise
import com.example.gymlog.model.Training
import com.example.gymlog.model.User
import com.example.gymlog.utils.Gender
import com.example.gymlog.utils.TrainingTypes
import java.util.Date
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

       fun getExercises(): List<Exercise> {
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
            val exercises = mutableListOf<Exercise>()
            val listSize = Random.nextInt(4, 10)
            while (exercises.size < listSize) {
                val title = exercisesStrings.random()
                exercises.find { it.title == title } ?: run {
                    val exercise = Exercise(
                        title = title,
                        repetitions = Random.nextInt(5, 20),
                        series = Random.nextInt(3, 8),
                        observations = "",
                        filters = emptyList()
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

        fun getUser() = User(gender = Gender.Male, height = 176, age = 21)

        fun getBmiList() = listOf(
            BmiInfo(
                gender = Gender.Male,
                weight = 66f,
                height = 176,
                age = 21,
                dateInMillis = Date().time
            ),
            BmiInfo(
                gender = Gender.Male,
                weight = 66f,
                height = 176,
                age = 21,
                dateInMillis = 1686020400000
            ),
            BmiInfo(
                gender = Gender.Male,
                weight = 55f,
                height = 176,
                age = 21,
                dateInMillis = 1677639600000
            ),
            BmiInfo(
                gender = Gender.Male,
                weight = 55f,
                height = 176,
                age = 21,
                dateInMillis = 1677726000000
            )
        )
    }

}