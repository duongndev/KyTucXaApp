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
                onNavigateToRegistration = {
                    // Điều hướng tới luồng đăng ký (Bắt đầu từ chọn phương thức nộp hồ sơ)
                    navController.navigate(Graphs.REGISTRATION) {
                        popUpTo(Graphs.SPLASH) { inclusive = true }
                    }
                }
            )
        }
    }
}
