package com.devmello.gymlog.core.navigation

import androidx.navigation.NavOptions
import com.devmello.gymlog.navigation.NavRoute

class NavDestination(
    val route: NavRoute,
    val navOptions: NavOptions? = null,
    val navMethod: NavMethod = NavMethod.SINGLE_TOP
)

enum class NavMethod {
    SINGLE_TOP,
    INCLUSIVE,
    DEFAULT
}
