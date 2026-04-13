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
                    // Điều hướng thẳng tới màn hình cập nhật hồ sơ
                    navController.navigate(Screen.UpdateProfile.route) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToRegistration = { draft ->
                    if (draft != null) {
                        // Lưu draft vào ViewModel của Registration hoặc truyền qua Route
                        navController.navigate(Graphs.REGISTRATION) {
                            popUpTo(Graphs.SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Graphs.REGISTRATION) {
                            popUpTo(Graphs.SPLASH) { inclusive = true }
                        }
                    }
                },
                onNavigateToPending = { registrationId ->
                    navController.navigate(Screen.RegistrationFlow.route) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToRequiresSupplement = { registrationId ->
                    // Giả định quay lại bước upload tài liệu
                    navController.navigate(Screen.DocumentUpload.route) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToOfflineInstructions = {
                    navController.navigate(Screen.DirectSubmissionGuide.route) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToStep1Residence = {
                    navController.navigate(Screen.RegistrationForm.route) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToStep2Temporary = {
                    navController.navigate(Screen.ResidenceRegistration.route) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToStep3Documents = {
                    navController.navigate(Screen.DocumentUpload.route) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToSubmitReady = {
                    navController.navigate(Screen.RegistrationFlow.route) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                }
            )
        }
    }
}
