package com.duongnd.kytucxa.core.navigations

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object AvatarUpload : Screen("avatar_upload")
    data object EmailVerification : Screen("email_verification/{email}") {
        fun createRoute(email: String) = "email_verification/$email"
    }
    data object SubmissionMethod : Screen("submission_method")
    data object RegistrationForm : Screen("registration_form")
    data object TemporaryForm : Screen("temporary_form")
    data object DocumentUpload : Screen("document_upload")
    data object DirectSubmissionGuide : Screen("direct_submission_guide")
    data object RegistrationFlow : Screen("registration_flow")
    data object Home : Screen("home")
    data object Room : Screen("room")
    data object RoomDetail : Screen("room/{roomId}") {
        fun createRoute(roomId: String) = "room/$roomId"
    }
    data object Payment : Screen("payment")
    data object PaymentHistory : Screen("payment_history")
    data object Support : Screen("support")
    data object Profile : Screen("profile")
    data object UpdateProfile : Screen("update_profile")
    data object CheckIn : Screen("check_in")
}

object Graph {
    const val AUTH = "auth_graph"
    const val MAIN = "main_graph"
}
