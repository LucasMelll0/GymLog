package com.example.gymlog.utils

import androidx.annotation.StringRes
import com.example.gymlog.R

enum class Sex {
    Male,
    Female;

    @StringRes
    fun stringRes(): Int {
        return when(this) {
            Male -> R.string.common_male
            Female -> R.string.common_female
        }
    }
}