package com.devmello.gymlog.core.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.devmello.gymlog.navigation.NavRoute

class NavigationManager {
    private val _destinations = MutableStateFlow<List<NavDestination>>(emptyList())
    val destinations = _destinations.asStateFlow()

    fun navigate(destination: NavDestination) {
        _destinations.update { it + destination }
    }

    fun navigate(route: NavRoute, method: NavMethod = NavMethod.SINGLE_TOP) {
        val destination = NavDestination(route, navMethod = method)
        _destinations.update { it + destination }
    }

    fun removeDestination(destinationToRemove: NavDestination) {
        _destinations.update { it.filterNot { it == destinationToRemove } }
    }
}
