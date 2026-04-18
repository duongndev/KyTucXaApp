package com.duongnd.kytucxa.core.navigations.graph.registration

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.duongnd.kytucxa.core.navigations.Graphs
import com.duongnd.kytucxa.core.navigations.Screen
import com.duongnd.kytucxa.feature.registration.tracking.RegistrationTrackingScreen
import com.duongnd.kytucxa.feature.registration.RegistrationViewModel
import com.duongnd.kytucxa.feature.registration.confirm.RegistrationConfirmScreen
import com.duongnd.kytucxa.feature.registration.confirm.RegistrationConfirmViewModel
import com.duongnd.kytucxa.feature.registration.preview.HtmlPreviewScreen
import com.duongnd.kytucxa.feature.registration.preview.HtmlPreviewViewModel
import com.duongnd.kytucxa.feature.registration.residence.ResidenceFormScreen
import com.duongnd.kytucxa.feature.registration.residence.ResidenceViewModel
import com.duongnd.kytucxa.feature.registration.submissionMethod.DirectSubmissionScreen
import com.duongnd.kytucxa.feature.registration.submissionMethod.SubmissionMethodScreen
import com.duongnd.kytucxa.feature.registration.temporary.TemporaryFormScreen
import com.duongnd.kytucxa.feature.registration.temporary.TemporaryViewModel
import com.duongnd.kytucxa.feature.registration.uploadDocument.DocumentUploadScreen
import com.duongnd.kytucxa.feature.registration.uploadDocument.UploadDocumentViewModel
import com.duongnd.kytucxa.feature.signature.SignatureScreen
import com.duongnd.kytucxa.feature.signature.SignatureViewModel

fun NavGraphBuilder.registrationNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SubmissionMethod.route,
        route = Graphs.REGISTRATION
    ) {
        composable(Screen.SubmissionMethod.route) { entry ->
            val viewModel = hiltViewModel<RegistrationViewModel>(entry)
            SubmissionMethodScreen(
                onOnlineSelected = {
                    navController.navigate(Screen.RegistrationForm.route)
                },
                onDirectSelected = {
                    navController.navigate(Screen.DirectSubmissionGuide.route)
                },
                onContinueExistingForm = { formId ->
                    navController.navigate(Screen.DocumentUpload.createRoute(formId))
                },
                onExit = {
                    navController.popBackStack()
                },
                registrationViewModel = viewModel
            )
        }

        composable(Screen.DirectSubmissionGuide.route) {
            DirectSubmissionScreen(
                onBack = {
                    navController.popBackStack()
                },
                onConfirm = {
                    navController.navigate(Screen.RegistrationForm.route)
                }
            )
        }

        composable(Screen.RegistrationForm.route) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.REGISTRATION)
            }
            val viewModel = hiltViewModel<ResidenceViewModel>(parentEntry)
            ResidenceFormScreen(
                viewModel = viewModel,
                onNext = { navController.navigate(Screen.TemporaryForm.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TemporaryForm.route) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.REGISTRATION)
            }
            val viewModel = hiltViewModel<TemporaryViewModel>(parentEntry)
            val registrationViewModel = hiltViewModel<RegistrationViewModel>(parentEntry)
            val formId by registrationViewModel.formId.collectAsState()

            TemporaryFormScreen(
                viewModel = viewModel,
                onNext = {
                    navController.navigate(Screen.DocumentUpload.createRoute(formId ?: ""))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DocumentUpload.route,
            arguments = listOf(navArgument("formId") { type = NavType.StringType })
        ) { 
            val uploadViewModel = hiltViewModel<UploadDocumentViewModel>()

            DocumentUploadScreen(
                viewModel = uploadViewModel,
                onNext = { navController.navigate(Screen.RegistrationConfirm.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RegistrationConfirm.route) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.REGISTRATION)
            }
            val confirmViewModel = hiltViewModel<RegistrationConfirmViewModel>()
            val registrationViewModel = hiltViewModel<RegistrationViewModel>(parentEntry)
            val signatureViewModel = hiltViewModel<SignatureViewModel>(parentEntry)
            val formId by registrationViewModel.formId.collectAsState()
            val safeFormId = formId ?: ""

            RegistrationConfirmScreen(
                viewModel = confirmViewModel,
                signatureViewModel = signatureViewModel,
                onViewResidenceDetail = {
                    navController.navigate(Screen.HtmlPreview.createRoute(safeFormId, "residence"))
                },
                onViewTemporaryDetail = {
                    navController.navigate(Screen.HtmlPreview.createRoute(safeFormId, "temporary"))
                },
                onViewDocumentsDetail = { /* Điều hướng preview giấy tờ */ },
                onOpenSignature = { navController.navigate(Screen.Signature.route) },
                onBack = { navController.popBackStack() },
                onSuccess = { navController.navigate(Screen.RegistrationFlow.route) }
            )
        }

        composable(
            route = Screen.HtmlPreview.route,
            arguments = listOf(
                navArgument("formId") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { entry ->
            val viewModel = hiltViewModel<HtmlPreviewViewModel>()
            val formId = entry.arguments?.getString("formId") ?: ""
            val type = entry.arguments?.getString("type") ?: ""

            HtmlPreviewScreen(
                viewModel = viewModel,
                formId = formId,
                type = type,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Signature.route) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.REGISTRATION)
            }
            val signatureViewModel = hiltViewModel<SignatureViewModel>(parentEntry)
            // SignatureViewModel sẽ lưu trữ chữ ký để dùng cho các màn hình khác
            SignatureScreen(
                viewModel = signatureViewModel,
                onConfirm = {
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RegistrationFlow.route) {
            RegistrationTrackingScreen(
                onUploadStampedForm = { formId ->
                    navController.navigate(Screen.DocumentUpload.createRoute(formId))
                },
                onViewDetail = { formId ->
                    // navController.navigate(Screen.RegistrationDetail.createRoute(formId))
                },
                onSkip = {
                    navController.navigate(Graphs.MAIN) {
                        popUpTo(Graphs.REGISTRATION) { inclusive = true }
                    }
                }
            )
        }
    }
}
