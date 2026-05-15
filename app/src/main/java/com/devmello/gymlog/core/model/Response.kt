package com.devmello.gymlog.core.model

sealed class Response<out T> {
    data class Success<out T>(val data: T?) : Response<T>()
    data class Error(val message: String? = null) : Response<Nothing>()
}
