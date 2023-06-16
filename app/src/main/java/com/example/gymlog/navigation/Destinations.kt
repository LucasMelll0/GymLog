package com.example.gymlog.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.gymlog.R

interface Destination {
    val route: String
    val title: Int?
}

object Home : Destination {
    override val route: String = "home"
    override val title: Int = R.string.home_destination
}

object Form : Destination {
    override val route: String = "form"
    override val title: Int = R.string.form_destination
    const val trainingIdArg = "training_id"
    val routeWithArgs = "$route/{$trainingIdArg}"
    val arguments = listOf(navArgument(trainingIdArg) {
        type = NavType.StringType
    })
}

object Log : Destination {
    override val route: String = "training_log"
    override val title: Int? = null
    const val trainingIdArg = "training_id"
    val routeWithArgs = "$route/{$trainingIdArg}"
    val arguments = listOf(navArgument(trainingIdArg) {
        type = NavType.StringType
    })
}

object Bmi : Destination {
    override val route: String = "bmi_historic"
    override val title: Int = R.string.bmi_destination
}

object Login : Destination {
    override val route: String = "login"
    override val title: Int? = null
}

object Register : Destination {
    override val route: String = "register"
    override val title: Int? = null
}

object Auth : Destination {
    override val route: String = "auth"
    override val title: Int? = null
}