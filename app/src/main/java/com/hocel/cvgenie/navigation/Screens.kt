package com.hocel.cvgenie.navigation

sealed class Screens(val route: String){
    object Login : Screens(route = "sign_in")
    object Register : Screens(route = "sign_up")
    object ForgotPassword : Screens(route = "forgot_password")
    object HomeScreen: Screens(route = "home_screen")
    object GenerateCVScreen: Screens(route = "generate_screen")

    object ViewPreCV: Screens(route = "view_pre_cv")
    object DetailsScreen: Screens(route = "details_screen")
}
