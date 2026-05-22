package com.devmello.gymlog.extensions

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import com.devmello.gymlog.navigation.NavRoute
import kotlin.reflect.KClass

fun NavDestination.hasRouteClass(routeClass: KClass<out NavRoute>): Boolean {
    return this.hasRoute(routeClass)
}


fun NavBackStackEntry.toNavRoute(): NavRoute? {
    return when {
        destination.hasRoute<NavRoute.Home>() -> NavRoute.Home
        destination.hasRoute<NavRoute.Form>() -> this.toRoute<NavRoute.Form>()
        destination.hasRoute<NavRoute.Log>() -> this.toRoute<NavRoute.Log>()
        destination.hasRoute<NavRoute.Bmi>() -> NavRoute.Bmi
        destination.hasRoute<NavRoute.DropdownTimer>() -> NavRoute.DropdownTimer
        destination.hasRoute<NavRoute.Stopwatch>() -> NavRoute.Stopwatch
        destination.hasRoute<NavRoute.UserProfile>() -> NavRoute.UserProfile
        destination.hasRoute<NavRoute.Auth>() -> NavRoute.Auth
        destination.hasRoute<NavRoute.Login>() -> NavRoute.Login
        destination.hasRoute<NavRoute.Register>() -> NavRoute.Register
        else -> null
    }
}