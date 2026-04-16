package com.duongnd.kytucxa.core.navigations.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.duongnd.kytucxa.core.navigations.Screen
import com.duongnd.kytucxa.core.navigations.Graphs
import com.duongnd.kytucxa.feature.splash.SplashScreen

fun NavGraphBuilder.splashNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Splash.route,
        route = Graphs.SPLASH
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Graphs.AUTH) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Graphs.MAIN) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToUpdateProfile = {
                    navController.navigate(Screen.UpdateProfile.route) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToRegistration = { draft ->
                    navController.navigate(Graphs.REGISTRATION) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToPending = { registrationId ->
                    navController.navigate(Screen.RegistrationFlow.route) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToRequiresSupplement = { registrationId ->
                    navController.navigate(Graphs.REGISTRATION) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                    // Xây dựng BackStack: Bước 1 -> Bước 2 -> Bước 3
                    navController.navigate(Screen.RegistrationForm.route)
                    navController.navigate(Screen.TemporaryForm.route)
                    navController.navigate(Screen.DocumentUpload.route)
                },
                onNavigateToOfflineInstructions = {
                    navController.navigate(Graphs.REGISTRATION) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                    navController.navigate(Screen.DirectSubmissionGuide.route)
                },
                onNavigateToStep1Residence = {
                    navController.navigate(Graphs.REGISTRATION) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                    navController.navigate(Screen.RegistrationForm.route)
                },
                onNavigateToStep2Temporary = {
                    navController.navigate(Graphs.REGISTRATION) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                    // Đi qua Bước 1 để Back quay lại được
                    navController.navigate(Screen.RegistrationForm.route)
                    navController.navigate(Screen.TemporaryForm.route)
                },
                onNavigateToStep3Documents = {
                    navController.navigate(Graphs.REGISTRATION) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                    // Đi qua Bước 1 -> Bước 2 để Back quay lại được
                    navController.navigate(Screen.RegistrationForm.route)
                    navController.navigate(Screen.TemporaryForm.route)
                    navController.navigate(Screen.DocumentUpload.route)
                },
                onNavigateToSubmitReady = {
                    navController.navigate(Graphs.REGISTRATION) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                    // Đi qua các bước trước đó
                    navController.navigate(Screen.RegistrationForm.route)
                    navController.navigate(Screen.TemporaryForm.route)
                    navController.navigate(Screen.DocumentUpload.route)
                    navController.navigate(Screen.RegistrationConfirm.route)
                }
            )
        }
    }
}
