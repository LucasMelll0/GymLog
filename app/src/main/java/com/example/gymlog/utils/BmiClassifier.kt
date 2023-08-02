package com.example.gymlog.utils

import kotlin.math.pow


class BmiClassifier(
    val gender: Gender,
    val weight: Float,
    val height: Int,
    val age: Int
) {
    val bmiValue: Float = calculateBmiValue()
    val idealWeight: List<Float> = getIdealWeightRange().sorted()

    private fun calculateBmiValue(weight: Float = this.weight) =
        weight / ((this.height.toFloat() / 100).pow(2))

    private fun getIdealWeightRange(): List<Float> {
        val currentRating = getRating()
        return if (currentRating != BmiRating.NormalWeight) {
            val idealWeightRange = mutableListOf<Float>()
            var weight = this.weight
            var bmiValue: Float
            if (currentRating == BmiRating.UnderWeight) {
                var rating = BmiRating.UnderWeight
                while (rating != BmiRating.Obesity && rating != BmiRating.PreObesity) {
                    weight++
                    bmiValue  = calculateBmiValue(weight)
                    rating = getRating(bmiValue)
                    if (rating == BmiRating.NormalWeight) {
                        idealWeightRange.add(weight)
                    }
                }
                idealWeightRange
            } else {
                var rating = BmiRating.Obesity
                while (rating != BmiRating.UnderWeight) {
                    weight--
                    bmiValue  = calculateBmiValue(weight)
                    rating = getRating(bmiValue)
                    if (rating == BmiRating.NormalWeight) {
                        idealWeightRange.add(weight)
                    }
                }
                idealWeightRange
            }
        } else {
            emptyList()
        }
    }

    fun getRating(bmiValue: Float = this.bmiValue): BmiRating {
        return if (age < 20) {
            when (gender) {
                Gender.Male -> {
                    maleBmiAdolescentClassifier(bmiValue)
                }

                else -> {
                    femaleBmiAdolescentClassifier(bmiValue)
                }
            }
        } else {
            adultBmiClassifier(bmiValue)
        }
    }

    private fun adultBmiClassifier(bmiValue: Float = this.bmiValue): BmiRating {
        return when (bmiValue) {
            in 0f..18.5f -> BmiRating.UnderWeight
            in 18.51f..24.9f -> BmiRating.NormalWeight
            in 24.91f..29.9f -> BmiRating.PreObesity
            in 29.91f..34.9f -> BmiRating.GradeOneObesity
            in 34.91f..39.9f -> BmiRating.GradeTwoObesity
            else -> BmiRating.GradeThreeObesity

        }
    }

    private fun maleBmiAdolescentClassifier(bmiValue: Float): BmiRating {
        return when (age) {
            11 -> {
                when (bmiValue) {
                    in 0f..14.83f -> BmiRating.UnderWeight
                    in 14.84f..20.35f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            12 -> {
                when (bmiValue) {
                    in 0f..15.24f -> BmiRating.UnderWeight
                    in 15.25f..21.12f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            13 -> {
                when (bmiValue) {
                    in 0f..15.73f -> BmiRating.UnderWeight
                    in 15.74f..21.93f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            14 -> {
                when (bmiValue) {
                    in 0f..16.18f -> BmiRating.UnderWeight
                    in 16.19f..22.77f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            15 -> {
                when (bmiValue) {
                    in 0f..16.59f -> BmiRating.UnderWeight
                    in 16.60f..23.63f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            16 -> {
                when (bmiValue) {
                    in 0f..17.01f -> BmiRating.UnderWeight
                    in 17.02f..24.45f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            17 -> {
                when (bmiValue) {
                    in 0f..17.31f -> BmiRating.UnderWeight
                    in 17.32f..25.28f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            18 -> {
                when (bmiValue) {
                    in 0f..17.54f -> BmiRating.UnderWeight
                    in 17.55f..25.95f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            19 -> {
                when (bmiValue) {
                    in 0f..17.80f -> BmiRating.UnderWeight
                    in 17.81f..26.36f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            else -> {
                when (bmiValue) {
                    in 0f..14.42f -> BmiRating.UnderWeight
                    in 14.43f..19.60f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }
        }
    }

    private fun femaleBmiAdolescentClassifier(bmiValue: Float): BmiRating {
        return when (age) {
            11 -> {
                when (bmiValue) {
                    in 0f..14.60f -> BmiRating.UnderWeight
                    in 14.61f..21.18f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            12 -> {
                when (bmiValue) {
                    in 0f..14.98f -> BmiRating.UnderWeight
                    in 14.99f..22.17f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            13 -> {
                when (bmiValue) {
                    in 0f..15.36f -> BmiRating.UnderWeight
                    in 15.37f..23.08f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            14 -> {
                when (bmiValue) {
                    in 0f..15.67f -> BmiRating.UnderWeight
                    in 15.68f..23.88f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            15 -> {
                when (bmiValue) {
                    in 0f..16.01f -> BmiRating.UnderWeight
                    in 16.02f..24.29f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            16 -> {
                when (bmiValue) {
                    in 0f..16.37f -> BmiRating.UnderWeight
                    in 16.38f..24.74f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            17 -> {
                when (bmiValue) {
                    in 0f..16.59f -> BmiRating.UnderWeight
                    in 16.60f..25.23f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            18 -> {
                when (bmiValue) {
                    in 0f..16.71f -> BmiRating.UnderWeight
                    in 16.72f..25.56f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            19 -> {
                when (bmiValue) {
                    in 0f..16.87f -> BmiRating.UnderWeight
                    in 16.88f..25.85f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }

            else -> {
                when (bmiValue) {
                    in 0f..14.23f -> BmiRating.UnderWeight
                    in 14.24f..20.19f -> BmiRating.NormalWeight
                    else -> BmiRating.Obesity
                }
            }
        }
    }
}

