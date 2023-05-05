package com.example.gymlog.utils

import androidx.annotation.StringRes
import com.example.gymlog.R

enum class TrainingTypes {
    CHEST {
        override fun stringRes(): Int = R.string.common_chest
    },
    BACK {
        override fun stringRes(): Int = R.string.common_back
    },
    BICEPS {
        override fun stringRes(): Int = R.string.common_biceps
    },
    TRICEPS {
        override fun stringRes(): Int = R.string.common_triceps
    },
    LEGS {
        override fun stringRes(): Int = R.string.common_legs
    },
    ABDOMEN {
        override fun stringRes(): Int = R.string.common_abdomen
    },
    SHOULDER {
        override fun stringRes(): Int = R.string.common_shoulder
    },
    CALF {
        override fun stringRes(): Int = R.string.common_calf
    },
    ARM {
        override fun stringRes(): Int = R.string.common_arm
    };

    @StringRes
    abstract fun stringRes(): Int
}