package com.devmello.gymlog.core.ui

import androidx.compose.material3.SnackbarDuration

data class Message(
    val id: Long = System.currentTimeMillis(),
    val text: String? = null,
    val textId: Int? = null,
    val duration: MessageDuration = MessageDuration.SHORT
)

enum class MessageDuration(val snackBarDuration: SnackbarDuration) {
    SHORT(snackBarDuration = SnackbarDuration.Short),
    LONG(snackBarDuration = SnackbarDuration.Long),
    INDEFINITE(snackBarDuration = SnackbarDuration.Indefinite);
}
