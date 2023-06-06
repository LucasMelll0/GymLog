package com.example.gymlog.utils

import androidx.annotation.StringRes
import com.example.gymlog.R

enum class BmiRating {
    UnderWeight {
        override fun stringRes(): Int = R.string.common_under_weight
    },
    NormalWeight {
        override fun stringRes(): Int = R.string.common_normal_weight
    },
    PreObesity {
        override fun stringRes(): Int = R.string.common_pre_obesity
    },
    Obesity {
        override fun stringRes(): Int = R.string.common_obesity
            },
    GradeOneObesity {
        override fun stringRes(): Int = R.string.common_grade_one_obesity
    },
    GradeTwoObesity {
        override fun stringRes(): Int = R.string.common_grade_two_obesity
    },
    GradeThreeObesity {
        override fun stringRes(): Int = R.string.common_grade_three_obesity
    };

    @StringRes
    abstract fun stringRes(): Int
}