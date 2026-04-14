package com.duongnd.kytucxa.core.navigations.graph.auth

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.duongnd.kytucxa.core.navigations.Screen
import com.duongnd.kytucxa.core.navigations.Graphs
import com.duongnd.kytucxa.feature.auth.login.LoginScreen
import com.duongnd.kytucxa.feature.auth.register.AvatarUploadScreen
import com.duongnd.kytucxa.feature.auth.verify.EmailVerificationScreen
import com.duongnd.kytucxa.feature.auth.register.RegisterScreen
import com.duongnd.kytucxa.feature.registration.submissionMethod.DirectSubmissionScreen
import com.duongnd.kytucxa.feature.registration.uploadDocument.DocumentUploadScreen
import com.duongnd.kytucxa.feature.registration.RegistrationSuccessScreen
import com.duongnd.kytucxa.feature.registration.RegistrationViewModel
import com.duongnd.kytucxa.feature.registration.submissionMethod.SubmissionMethodScreen
import com.duongnd.kytucxa.feature.registration.residence.ResidenceFormScreen
import com.duongnd.kytucxa.feature.registration.residence.ResidenceViewModel
import com.duongnd.kytucxa.feature.registration.temporary.TemporaryFormScreen
import com.duongnd.kytucxa.feature.registration.temporary.TemporaryViewModel

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Login.route,
        route = Graphs.AUTH
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Graphs.MAIN) {
                        popUpTo(Graphs.AUTH) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToCompleteProfile = {
                    navController.navigate(Screen.UpdateProfile.route) {
                        popUpTo(Graphs.AUTH) { inclusive = true }
                    }
                },
                onNavigateToRegistration = {
                    navController.navigate(Screen.SubmissionMethod.route) {
                        popUpTo(Graphs.AUTH) { inclusive = true }
                    }
                },
                onNavigateToVerify = { email ->
                    navController.navigate(Screen.EmailVerification.createRoute(email))
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { email ->
                    navController.navigate(Screen.EmailVerification.createRoute(email))
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.EmailVerification.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                email = email,
                onVerificationSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SubmissionMethod.route) {
            SubmissionMethodScreen(
                onOnlineSelected = { navController.navigate(Screen.AvatarUpload.route) },
                onDirectSelected = { navController.navigate(Screen.DirectSubmissionGuide.route) },
                onContinueExistingForm = { formId ->
                    navController.navigate(Screen.AvatarUpload.route)
                },
                onExit = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Graphs.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.DirectSubmissionGuide.route) {
            DirectSubmissionScreen(
                onConfirm = {
                    navController.navigate("registration_success") {
                        popUpTo(Screen.SubmissionMethod.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AvatarUpload.route) {
            AvatarUploadScreen(
                onNext = { navController.navigate(Screen.RegistrationForm.route) },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.RegistrationForm.route) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.AUTH)
            }
            val viewModel = hiltViewModel<ResidenceViewModel>(parentEntry)
            ResidenceFormScreen(
                viewModel = viewModel,
                onNext = { navController.navigate(Screen.DocumentUpload.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TemporaryForm.route) {entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.AUTH)
            }
            val viewModel = hiltViewModel<TemporaryViewModel>(parentEntry)
            TemporaryFormScreen(
                viewModel = viewModel,
                onNext = { navController.navigate(Screen.DocumentUpload.route) },
                onBack = { navController.popBackStack() }
            )

        }
        
        composable(Screen.DocumentUpload.route) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.AUTH)
            }
            val viewModel = hiltViewModel<RegistrationViewModel>(parentEntry)
            DocumentUploadScreen(
                viewModel = viewModel,
                onNext = { navController.navigate("registration_success") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("registration_success") {
            RegistrationSuccessScreen(
                onContinue = {
                    navController.navigate(Graphs.MAIN) {
                        popUpTo(Graphs.AUTH) { inclusive = true }
                    }
                }
            )
        }
    }
}
