package com.devmello.gymlog.core.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoadingManager {
    private val _isLoading = MutableStateFlow(false)
    private var _text by mutableStateOf<String?>(null)
    private var _stringId by mutableStateOf<Int?>(null)
    val text: String? get() = _text
    val stringId: Int? get() = _stringId
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun show(loadingText: String? = null, @StringRes textId:  Int? = null) {
        _text = loadingText
        _isLoading.value = true
    }

    fun hide() {
        _isLoading.value = false
        _text = null
        _stringId = null
    }

}