package com.example.gymlog.extensions

import androidx.core.text.isDigitsOnly

fun String.isZeroOrEmpty(): Boolean {
    return if (this.isDigitsOnly() && this.isNotEmpty()) {
        this.toInt() == 0
    } else {
        this.isEmpty()
    }
}

fun String.capitalizeAllWords(): String = this.split(" ").joinToString(
    separator = " ",
    transform = { it.replaceFirstChar { firstLetter -> firstLetter.uppercase() } })