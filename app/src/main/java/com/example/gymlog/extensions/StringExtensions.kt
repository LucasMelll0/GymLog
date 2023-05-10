package com.example.gymlog.extensions

import androidx.core.text.isDigitsOnly

fun String.isZeroOrEmpty(): Boolean {
    return if (this.isDigitsOnly() && this.isNotEmpty()) {
        this.toInt() == 0
    }else {
        this.isEmpty()
    }
}