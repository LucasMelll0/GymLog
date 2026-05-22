package com.devmello.gymlog.utils

sealed class State<out T> {
    data class Success<out T>(val data: T) : State<T>()
    data class Error(val message: String) : State<Nothing>()
    object Loading : State<Nothing>()
}