package com.devmello.gymlog.core.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoadingManager {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun show() {
        _isLoading.value = true
    }

    fun hide() {
        _isLoading.value = false
    }

}