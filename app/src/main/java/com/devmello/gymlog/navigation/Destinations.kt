package com.devmello.gymlog.navigation

import androidx.navigation.navDeepLink
import com.devmello.gymlog.R
import com.devmello.gymlog.navigation.NavRoute.Home
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoute {
    @Serializable
    object Home : NavRoute

    @Serializable
    data class Form(val trainingId: String? = null) : NavRoute

    @Serializable
    data class Log(val trainingId: String? = null) : NavRoute

    @Serializable
    object Bmi : NavRoute

    @Serializable
    object Login : NavRoute

    @Serializable
    object Register : NavRoute

    @Serializable
    object Auth : NavRoute

    @Serializable
    object DropdownTimer : NavRoute

    @Serializable
    object Stopwatch : NavRoute

    @Serializable
    object UserProfile : NavRoute
}

interface Destination {
    val route: String
    val navRoute: NavRoute
    val title: Int?
    val icon: Int?
    val isSubscreen: Boolean
}

object HomeDestination : Destination {
    override val route: String = "home"
    override val navRoute: NavRoute = Home
    override val title: Int = R.string.home_destination
    override val icon: Int = R.drawable.ic_home
    override val isSubscreen: Boolean = false
}

object BmiDestination : Destination {
    override val route: String = "bmi_historic"
    override val navRoute: NavRoute = NavRoute.Bmi
    override val title: Int = R.string.bmi_destination
    override val icon: Int = R.drawable.ic_weight
    override val isSubscreen: Boolean = false
}

object DropdownTimerDestination : Destination {
    override val route: String = "dropdown_timer"
    override val navRoute: NavRoute = NavRoute.DropdownTimer
    override val title: Int = R.string.dropdown_timer_destination
    override val icon: Int = R.drawable.ic_hourglass
    override val isSubscreen: Boolean = false
    val deepLinks = listOf(
        navDeepLink<NavRoute.DropdownTimer>(basePath = "gymlog://${route}")
    )
}

object StopwatchDestination : Destination {
    override val route: String = "stopwatch"
    override val navRoute: NavRoute = NavRoute.Stopwatch
    override val title: Int = R.string.stopwatch_destination
    override val icon: Int = R.drawable.ic_stopwatch
    override val isSubscreen: Boolean = false
    val deepLinks = listOf(
        navDeepLink<NavRoute.Stopwatch>(basePath = "gymlog://${route}")
    )
}

object UserProfileDestination : Destination {
    override val route: String = "user_profile"
    override val navRoute: NavRoute = NavRoute.UserProfile
    override val title: Int = R.string.user_profile_title
    override val icon: Int = R.drawable.ic_person
    override val isSubscreen: Boolean = false
}
