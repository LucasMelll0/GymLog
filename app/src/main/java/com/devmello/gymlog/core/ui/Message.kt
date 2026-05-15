package com.devmello.gymlog.core.ui

data class Message(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val duration: MessageDuration = MessageDuration.SHORT
)

enum class MessageDuration {
    SHORT,
    LONG
}
