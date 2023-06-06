package com.example.gymlog.utils

import androidx.annotation.StringRes
import com.example.gymlog.R

enum class Gender {
    Male {
        override fun stringRes(): Int = R.string.common_male
    },
    Female {

        override fun stringRes(): Int = R.string.common_female
    };

    @StringRes
    abstract fun stringRes(): Int
}