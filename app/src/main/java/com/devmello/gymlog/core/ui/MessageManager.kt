package com.devmello.gymlog.core.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MessageManager {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun postMessage(text: String, duration: MessageDuration = MessageDuration.SHORT) {
        val message = Message(text = text, duration = duration)
        _messages.update { currentList -> currentList + message }
    }

    fun removeMessage(messageId: Long) {
        _messages.update { currentList ->
            currentList.filterNot { it.id == messageId }
        }
    }

}