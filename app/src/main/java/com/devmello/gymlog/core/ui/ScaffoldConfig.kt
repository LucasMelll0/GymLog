package com.devmello.gymlog.core.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

typealias CanPop = Boolean
data class ScaffoldConfig(
    val title: String = "",
    val fab: @Composable () -> Unit = {},
    val showTopBar: Boolean = true,
    val tobBarActions: @Composable RowScope.() -> Unit = {},
    val showBottomBar: Boolean = true,
    val bottomBar: @Composable () -> Unit = {},
    val onNavigateBack: () -> CanPop = {true},
    val drawerGesturesEnabled: Boolean = true
)

class ScaffoldManager {
    private val _config = MutableStateFlow(ScaffoldConfig())
    val config = _config.asStateFlow()

    fun updateConfig(newConfig: ScaffoldConfig) {
        _config.value = newConfig
    }
}