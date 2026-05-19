package com.devmello.gymlog.core.navigation

class NavDestination(
    val route: String,
    val destinationArgs: List<String>? = null,
    val navMethod: NavMethod = NavMethod.SINGLE_TOP
) {
    val formatedDestination: String get() {
        if(destinationArgs.isNullOrEmpty()) return route.trim()
        val args = destinationArgs.joinToString(separator = "/") { it.trim() }
        return "${route.trim()}/$args"
    }
}


enum class NavMethod {
    SINGLE_TOP,
    INCLUSIVE
}